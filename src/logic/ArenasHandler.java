package logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by davidnavalho on 24/01/16.
 */
//Handles Arenas...lol
//Objectives: Prioritized Arenas: only do the higher level arenas first; they need a condition to run
    //once the condition is satisfied, the lower level arenas start being done;
    //the condition must reset itself every once in a while
    //also, the arena may not even be available, so this needs to be handled correctly too because:
        //it may not be available because it doesn't exist;
        //it may not be available because of some bug;
            //when it does not exist, sleep on it, but come back to it earlier then if it's requirements are done
            //when it exists, but the requirements are met, then it's the same, sleep again
public class ArenasHandler {

    private LinkedList<LinkedList<Arena>> arenas;
    public Properties props;

    public ArenasHandler(Properties props){
        this.props = props;
        this.arenas = new LinkedList<LinkedList<Arena>>();
    }

    private LinkedList<Arena> findArena(int priority){
        Iterator<LinkedList<Arena>> it = this.arenas.iterator();
        while(it.hasNext()){
            LinkedList<Arena> arenaList = it.next();
            if(arenaList.getFirst().priority==priority)
                return arenaList;
        }
        return null;
    }

    private void addOrdered(LinkedList<Arena> newArenaList){
        int i=0;
        Iterator<LinkedList<Arena>> it = this.arenas.iterator();
        while(it.hasNext()) {
            LinkedList<Arena> arenaList = it.next();
            if(arenaList.getFirst().priority<newArenaList.getFirst().priority) {
                this.arenas.add(i, newArenaList);
                return;
            }
            i++;
        }
        this.arenas.add(i,newArenaList);
    }

    public void addArena(Arena arena){
        int priority = arena.priority;
        LinkedList<Arena> ll = this.findArena(priority);
        if(ll!=null)
            ll.add(arena);//this is enough
        else{
            ll = new LinkedList<Arena>();
            ll.add(arena);
            this.addOrdered(ll);
        }
    }

    private boolean checkAvailableToRun(LinkedList<Arena> arenaList){
        Iterator<Arena> it = arenaList.iterator();
        while(it.hasNext()){
            Arena arena = it.next();
            if(arena.arenaIsActive())
                return true;
        }
        return false;
    }

    private LinkedList<Arena> getNextPriorityArenas(){
        Iterator<LinkedList<Arena>> it = this.arenas.iterator();
        while(it.hasNext()){
            LinkedList<Arena> arenaList = it.next();
            if(this.checkAvailableToRun(arenaList))
                return arenaList;
        }
        System.out.println("There are no active Arenas to run!");
        return null;//should never reach this....
    }

    //On what does running an arena consist?
    //1 - check if it's active based on sleep timer and check if it's not done (so it becomes inactive)
    //2 - if it's active, check if it has remaining iterations
    //3 - if it has remaining iterations, run it, decrement iterator and return true
        //3.5 - when running it, it's state may become inactive (i.e. found no rewards)
    //4 - if it does not have remaining iterations, skip to the next arena at the same priority level
    //5 - if no arenas ran, reset all iterators
    //6 - go back to 1
        //6.5 - unless they all became inactive after the first round! then return false
    private boolean runIfPossible(Arena arena){
        if(arena.arenaIsActive())
            return arena.runArena();
        return false;
    }

    private boolean anyArenaActive(LinkedList<Arena> arenaList){
        Iterator<Arena> it = arenaList.iterator();
        while(it.hasNext()){
            if(it.next().arenaIsActive())
                return true;
        }
        return false;
    }

    private boolean availableIterations(LinkedList<Arena> arenaList){
        Iterator<Arena> it = arenaList.iterator();
        while(it.hasNext())
            if(it.next().hasAvailableIterations())
                return true;
        return false;
    }

    private void resetIterations(LinkedList<Arena> arenaList){
        Iterator<Arena> it = arenaList.iterator();
        while(it.hasNext()){
            it.next().resetIterations();
        }
    }

    private boolean executeArena(LinkedList<Arena> arenaList){
        int i = 0;
        System.out.print("Searching for arenas.");
        while(anyArenaActive(arenaList)) {
            System.out.print(".");
            //if some arenas are active, their iterations may all be at zero, so check those
            if(!this.availableIterations(arenaList)) {
                this.resetIterations(arenaList);
            }
            Iterator<Arena> it = arenaList.iterator();
            while (it.hasNext()) {
                Arena arena = it.next();
                System.out.print("_");
                if(this.runIfPossible(arena)) {
                    System.out.println();
                    return true;//it should quit this loop eventually! TODO: make sure it does...
                }
            }
        }
        System.out.println();
        return false;
    }

    public void runNextArena(){
        //get the highest priority arenas;
        //if at least one of them is valid, 'execute' it
            //check validity (other function) to execute (includes sleep), or provide false if they're all inactive
            //if they're all innactive, go for next priority arenas -> this can happen on a next iteration, and that's ok
        LinkedList<Arena> arenaList = this.getNextPriorityArenas();
        System.out.println("Found arenas to run with priority: "+arenaList.getFirst().priority+", total: "+arenaList.size()+" ["+arenaList.getFirst().arena+"]");
        this.executeArena(arenaList);
    }
}
