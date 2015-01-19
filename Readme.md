# Blind Auction

## Project Overview

This project is created as a final project for class Advanced Topics in Multi-Agent Systems by Professor Ogawa of Ritsumeikan University. This project is solely made for research and educational purpose only.

This project implements [First-price Sealed-bid Auction](http://en.wikipedia.org/wiki/Auction_theory) using [JADE Framework](http://jade.tilab.com/). Excerpting from Wikipedia, "First-price sealed-bid auctions in which bidders place their bid in a sealed envelope and simultaneously hand them to the auctioneer. The envelopes are opened and the individual with the highest bid wins, paying the amount bid."

Three agents are available here. Auctioneer, BidderComp, and BidderHuman. Auctioneer is the auctioneer agent, which holds the main auction event. 

Auctioneer is provided with a simple JFrame GUI to add more items to its auction catalogue. BidderComp and BidderHuman are both the bidder agents, where BidderComp is an agent automatically controlled by the computer, and BidderHuman is controlled by a (human) user. BidderComp has the ALL-IN strategy, which means it always bids any item introduced in an auction, using the maximum budget (money) it has in its wallet. Once its money runs out, the agent terminates.

A user can participate to the auction using BidderHuman agent. A JFrame GUI is provided for adding more money to the wallet and to send bids. The user can also refuse to participate on an item auction.

### How to operate Auctioneer

![](https://github.com/ardiyu07/jade-blind-auction/blob/master/blob/auctioneer.png)

#### Adding item

Fill in `Item Name` and the `Initial Price`, and click `Add`.

### How to operate BidderHuman

![](https://github.com/ardiyu07/jade-blind-auction/blob/master/blob/bidder.png)

#### Sending bid

Fill in `Bid Price`, and click `Bid`.

### Refusing to participate in an item auction

Click `Rest`.

### Adding more money

Fill in `Add money`, and click `Add`, then the GUI window will be refreshed.

## Prerequisites

- [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Jade - JAVA Agent Development Framework](http://jade.tilab.com/)
- [ant (optional)](http://ant.apache.org/bindownload.cgi)

## How to build with ant
    
    $ ant

## How to build without ant

    $ javac -d classes -cp "lib/jade.jar" src/blindAuction/* 

## Execute 

### Execute sample
#### For Windows
Run runExamples.bat   

#### For Linux
    $ cd bin
    $ ./runExamples.sh

### Execute Auctioneer, BidderComp, and BidderHuman
#### For Windows
Run the .bat files, but make sure you run runAuctioneer.bat first.

#### For Linux

Open a terminal

    $ cd bin
    $ ./runAuctioneer.sh
    
Open another terminal to run a computer bidder

    $ cd bin
    $ ./runBidderComp.sh
    
Open another terminal to run a human bidder

    $ cd bin
    $ ./runBidderHuman.sh

## Licensing

[GPL LGPL v2.1](https://www.gnu.org/licenses/lgpl-2.1.html)

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

## Acknowledgement

This project is derived from [bookTrading](http://jade.tilab.com/documentation/examples/book-trading/)

