#!/bin/sh
java -cp ../lib/jade.jar:../lib/blindAuction.jar jade.Boot -agents "a1:blindAuction.BidderComp;a2:blindAuction.BidderComp;a3:blindAuction.BidderComp;a4:blindAuction.Auctioneer"
