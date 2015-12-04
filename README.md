# walrepo1_fluffy-octo-kidney

This is my working repository for the Ticket Service Homework that provides discovery, temporary hold, and final reservation
of seats within a high-demand perofmrance venue.

There is a diagram of the venue and some basic venue data, which provides the problem space description.  Additionally
there is an interface describing the three main functions of the system:

  * Show the number of available seats
  * Place a hold on a block of seats for a customer
  * Reserve and commit the hold
  
If the hold is not committed within a specific timeframe, it expires.

## Approach

I generally try to describe the problem space with as much definition and little effort as possible first.  In the
past this might require switching to modeling tools, but at this point main-stream frameworks are about the same
level as modeling tools and so through:

  * An object model of Performance, Venues, Seats, and Holds
  * A service description for the interface
  * A set of tests working through that service
  
all done in mostly declarative code, I worked through a first pass at what the system had to do.  

### Pass-1

Because it is a slightly higher-level language that is very Java/Spring compatible, I chose to work in Grails
and then accept potentially having to convert it later.  

The Pass-1 is described here:

See [tas2_Part1](doc/tas2_Part1.md)


