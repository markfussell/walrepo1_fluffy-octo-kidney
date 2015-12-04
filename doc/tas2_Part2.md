# TSA2

The ticketServiceApp2 represents the first pass (there was no '1') through the ticketServiceApplication

By the end of Part1 we had the core domain model, a mocked/simplified service interface, and
a test against that interface.

## Core algorithm

The big issue with the problem is the phrase "high-demand performance venue".  If there are 1000 requests 
per second for seats and the seats have a common preference order then everyone is vying for:

   * L1: 1250
   * L2: 2000
   * L3: 1500
   * L4: 1500
   
6250 seats.  Each request could be 1-6+ seats, but assume just '4' for the moment.  That is 4000 seats requested
per second.  Clearly a bit unrealistically high, but erring high does not harm anything unless you over-build.

There are all kinds of different solutions to this mass assault, but a hyper-speed version might make a few interesting
assumptions:

  * Lower levels are better than higher levels
  * Being near center is better than being near the side (no side-requests)
  * Being on the same row is required 
  * People don't mind being moved to better seats after their reservation
   
The first three are to enable very rapid determination of seating, where the last one is to enable 'hole collection' 
if people don't commit to their holds.

### Details

Because of the 'Center' affinity, each row needs two 'cursors':

   * One going right from center-right (26 through 50)
   * One going left from  center-left (1 through 25)
   
Each cursor has a value that is lower for being a lower level and lower row.  If we 'cheat' and multiply the level by 1000, 
we would get:
 
  * 1001 – For row 1 level 1
  * ...
  * 4015 – For row 15 level 4
  
Eventally we might penalize a cursor for being 'wider' than another cursor, in which case we could add the current increment
from dead center, but that requires cursor reordering which I am avoiding for the moment.

So if we have 75 rows in total, we have 150 cursors where each cursor:
  
  * Knows its value (a constant) and knows how many seats it has left
  
So now with each customer request, you walk through the cursors (starting at the minimum level the user wants and ending 
at the maximum) until a cursors accepts the SeatHold.  The cursor then bumps its counter and gives the collection of
seats to the SeatHold.

#### Why?

Basically this is totally focused on an incredibly fast pipeline that does not slow down due to synchronization collisions.
By having 150 cursors that are each individually responsible for handling a request and an incredibly simple algorithm
for handling that request, we could get through a lot of requests per 'tick'.  As many as 150 'threads' could be executing
simultaneously and doing something as simple as 'is remaining < request ?'.  

### More Fun

Because the details of the algorithm are hidden, you could also solve any 'rush' problem with a 'cursor pattern' and 'hop'
algorithm:

  * A dozen requests hit at the same second.  
  
By default, these would be injected one-by-one into the lowest number (say 1001) cursor and then speed merrily on their
way.  Although the algorithm is 
 super-fast, we do have a starter backlog issue: only one enters the system at a time.  To resolve this, 
 we could alternatively have requests with different 'cursor patterns' (one does odds and then evens for six rows,
 the second does evens and then odds for six rows) so you can inject more than one at a time into the system.  If
 you next cursor is busy, you 'hop' over it and go to the next cursor in your pattern.  You might do a final revisit 
 to any hopped cursors, but you ultimately move to the next sublevel after visiting all the cursors and 
   
### Improve / Degrade Gracefully

Ultimately we want to provide the best possible answer, but that includes dealing with load gracefully.  Putting all
the above together into one full model

  * We have cursors that are ordered in value of their seats
  * We have requests for seats that ideally start at the highest valued seats and then works to lowered valued seats
  * Each cursor-request is process atomically and very rapidly
  * But if there is a backlog than a 'sub-level' or 'row-set' size greater than '1' is created
    * Based on different patterns or even random visitation, each request visits all or some of the rows in the row-set
    * After which it goes to the next row-set
  * As the backlog improves, the row-set size returns to '1'.
  
In a shopping sense, this degradation response is similar to opening new registers.

## Make it so




   

   