package blindAuction.agent;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

import blindAuction.AuctioneerGUI;

/**
 * JADE agent representing an auctioneer of an auction.
 * It has single sequential behavior representing its lifecycle.
 * <ol>
 *  <li> 
 * </ol>
 * 
 * As an init param this agent accepts the number of rounds to be played.
 */
public class Auctioneer extends Agent {

	// The catalogue of items for sale (maps the title of a item to its price)
	private Hashtable catalogue;

	// The GUI by means of which the user can add items in the catalogue
	private AuctioneerGUI myGui;

    // Show whether auction has started
    private boolean auctionStarted = false;

    // The template to receive replies
    static MessageTemplate mt; 

    // The list of known bidders
	static AID[] bidders;

    // The bidder who provides the best offer
    static AID bestBidder;

    // The most updated best offered price
    static int bestPrice;

    @Override
    protected void setup() {

        // Printout a welcome message
		System.out.println("Hello! Auctioneer "+getAID().getName()+" is ready.");

		// Create the catalogue
		catalogue = new Hashtable();

		// Create and show the GUI 
		myGui = new AuctioneerGUI(this);
		myGui.showGui();
       
        // Sleep for 20 sec, so that user can add item to cataloge
        System.out.println("Please add an item for auction within 20 seconds..");
        try {
            Thread.currentThread().sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Add a TickerBehaviour that schedules a request to bidders every minute
        addBehaviour(new ActionPerMinute(this));
    }

	// Put agent clean-up operations here
	protected void takeDown() {

		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Close the GUI
		myGui.dispose();

		// Printout a dismissal message
		System.out.println("Auctioneer "+getAID().getName()+" terminating.");
	}

	/**
       This is invoked by the GUI when the user adds a new item for sale
    */
	public void updateCatalogue(final String title, final int price) {

		addBehaviour(
                     new OneShotBehaviour() {
                         public void action() {
                             catalogue.put(title, new Integer(price));
                             System.out.println(title+" inserted into catalogue. Price = "+price);
                         }
                     }
                     );
	}

	/**
       This is invoked to delete item
    */
	public Integer removeItemFromCatalogue(final String title) {

        Integer price = null;
        price = (Integer) catalogue.get(title);
		addBehaviour(
                     new OneShotBehaviour() {
                         public void action() {
                             catalogue.remove(title);
                         }
                     }
                     );
        return price;
	}

    public boolean isCatalogueEmpty() {
        return catalogue.isEmpty();
    }

    public String getFirstItemName() {
        return (String)catalogue.keySet().toArray()[0];
    }
}

// Add a TickerBehaviour that schedules an auction to bidders every minute
class ActionPerMinute extends TickerBehaviour {

    private Auctioneer myAgent;

    private boolean biddersFound = false;
    private boolean CFPSent = false;
    private boolean bidsReceived = false;

    private String currentItemName;

    public ActionPerMinute(Auctioneer agent) {
        super(agent, 60000);
        myAgent = agent;
    }

    @Override
    protected void onTick(){

        // Initialize all conditions
        biddersFound = false;
        CFPSent = false;
        bidsReceived = false;
        
        // If there is any item to sell
        if (!myAgent.isCatalogueEmpty()) {

            currentItemName = myAgent.getFirstItemName();
            System.out.println("Starting auction for item " + currentItemName);

            // Find Bidder
            if (!biddersFound) {
                myAgent.addBehaviour(new FindBidder(myAgent));
                biddersFound = true;
            }
        
            // Send CFP to all bidders
            if (!CFPSent) {
                myAgent.addBehaviour(new SendCFP(myAgent, currentItemName));
                CFPSent = true;
            }
            
            // Receive all proposals/refusals from bidders and find the highest bidder
            if (CFPSent) {
                myAgent.addBehaviour(new ReceiveBids(myAgent));
                bidsReceived = true;
            }
            
            // Send the request order to the bidder that provided the best offer
            if (bidsReceived) {
                myAgent.addBehaviour(new AnnounceWinnerAndUpdateCatalogue(myAgent, currentItemName));
            }
        }        
    }
}

class FindBidder extends CyclicBehaviour {

    private Auctioneer myAgent;

    public FindBidder(Auctioneer agent) {
        super(agent);
        myAgent = agent;
    }
    
    public void action() {
        // Update the list of bidders
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("blind-bidders");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template); 
            System.out.println("Found the following bidders:");
            myAgent.bidders = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                myAgent.bidders[i] = result[i].getName();
                System.out.println(myAgent.bidders[i].getName());
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}

/**
   Send CFP to all bidders
*/
class SendCFP extends CyclicBehaviour {

    private Auctioneer myAgent;
    private String itemName;

    public SendCFP(Auctioneer agent, String itemName) {
        super(agent);
        myAgent = agent;
        this.itemName = itemName;
    }
    
    public void action() {
        // Send the cfp to all bidders
        System.out.println("Sending CFP to all bidders..");
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (int i = 0; i < myAgent.bidders.length; ++i) {
            cfp.addReceiver(myAgent.bidders[i]);
        } 
        cfp.setContent(this.itemName);
        cfp.setConversationId("blind-bid");
        cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
        myAgent.send(cfp);

        // Prepare message template
        myAgent.mt = MessageTemplate.and(MessageTemplate.MatchConversationId("blind-bid"),
                                         MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));        
    }
}

/**
   Receive all proposals/refusals from bidders and find the highest bidder
*/
class ReceiveBids extends CyclicBehaviour {

    private Auctioneer myAgent;

    private int repliesCnt = 0; // The counter of replies from seller agents

    public ReceiveBids(Auctioneer agent) {
        super(agent);
        myAgent = agent;
    }
    
    public void action() {
        // Receive all proposals/refusals from seller agents
        ACLMessage reply = myAgent.receive(myAgent.mt);
        if (reply != null) {
            // Reply received
            if (reply.getPerformative() == ACLMessage.PROPOSE) {

                // This is an offer 
                int price = Integer.parseInt(reply.getContent());
                if (myAgent.bestBidder == null || price > myAgent.bestPrice) {
                    // This is the best offer at present
                    myAgent.bestPrice = price;
                    myAgent.bestBidder = reply.getSender();
                }
            }

            repliesCnt++;
        }
        else {
            block();
        }
        
        System.out.println("Waiting for all bids..");
        if (repliesCnt < myAgent.bidders.length) {
            // We havent received all bids
            block();
        }
    }
}

/**
   Send the request order to the bidder that provided the best offer
*/
class AnnounceWinnerAndUpdateCatalogue extends CyclicBehaviour {

    private Auctioneer myAgent;

    private String itemName;

    public AnnounceWinnerAndUpdateCatalogue(Auctioneer agent, String itemName) {
        this.itemName = itemName;
        myAgent = agent;
    }
    
    public void action() {
        System.out.println("Announcing Winner..");

        // Send the purchase order to the seller that provided the best offer
        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        order.addReceiver(myAgent.bestBidder);
        order.setContent(this.itemName);
        order.setConversationId("blind-bid");
        order.setReplyWith("order"+System.currentTimeMillis());

        Integer price = (Integer) myAgent.removeItemFromCatalogue(this.itemName);
        if (price != null) {
            System.out.println(itemName+" sold to agent "+ myAgent.bestBidder.getName());
        }
        else {
            // The requested item has been sold to another buyer..somehow
            order.setPerformative(ACLMessage.FAILURE);
            order.setContent("not-available");
        }
        myAgent.send(order);
    }
}
