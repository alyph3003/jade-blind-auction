package blindAuction.behaviour.Auctioneer;

import jade.core.behaviours.*;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import blindAuction.agent.Auctioneer;

/**
   This is the behaviour used by Auctioneer to request bids
*/
public class HandleBid extends Behaviour {

    private int repliesCnt = 0;
    private MessageTemplate mt;

    public HandleBid(Auctioneer agent) {
        super(agent);
    }

    @Override
    protected void action(){
        
    }
}

/**
   Send CFP to all bidders
*/

/**
   Receive all proposals/refusals from bidders and find the highest bidder
*/

/**
   Send the request order to the bidder that provided the best offer
*/

/**
   Receive the request order, renew catalogue and give the item to bidder
*/
