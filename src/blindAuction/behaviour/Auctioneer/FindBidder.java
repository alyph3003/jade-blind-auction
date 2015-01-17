package blindAuction.behaviour.Auctioneer;

import jade.core.behaviours.*;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import blindAuction.agent.Auctioneer;

// Add a TickerBehaviour that schedules a request to seller agents every perTime
public class FindBidder extends TickerBehaviour {

    public FindBidder(Auctioneer agent, int perTime) {
        super(agent, perTime);
    }

    @Override
    protected void onTick(){
        // Update the list of seller agents
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("blind-auction");
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
