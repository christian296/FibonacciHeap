
/**
 * christians
 * christian shakkour
 * 322828518
 */
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{

    private int size;
    private HeapNode min;
    private HeapNode first;
    private int numOfTrees;
    private int numOfMarked;

    private static int totalLinks;
    private static int totalCuts;


   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *
    * O(1)
    */
    public boolean isEmpty()
    {
        if(this.size==0){
    	    return true; }

        return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    *
    * O(1)
    */
    public HeapNode insert(int key)
    {
        HeapNode newnode = new HeapNode(key);

        if(isEmpty()){

            first=newnode;
            min=newnode;

        }
        else {

            newnode.setPrev(first.getPrev());
            newnode.getPrev().setNext(newnode);
            first.setPrev(newnode);
            newnode.setNext(first);

            first = newnode;
            if (newnode.getKey() < min.getKey()) {
                min = newnode;
            }
        }
        size = size + 1;
        numOfTrees = numOfTrees + 1;
    	return newnode;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    * O(n) Worst Case
    * O(logn) Amortized
    */
    public void deleteMin() {
        //heap is empty
        if (isEmpty()) {
            return;
        }
        //one node
        if (size == 1) {
            size--;
            this.min = null;
            this.first = null;
            this.numOfTrees--;
            return;
        }

        //need successive linking
        else {
            //one tree
        if (numOfTrees == 1) {
            size--;
            this.first = min.child;

            //update numoftrees
            numOfTrees = min.getRank();
            nullParents_Mark(first);
        }

        else {
            // min has no children
            if(this.min.getChild()==null){
            min.getPrev().setNext(min.getNext());
            min.getNext().setPrev(min.getPrev());

            //update numoftrees
            numOfTrees--;
            size--;
            //update first
            if(min==first){
                first=min.getNext();
            }

        }
        else {
            size--;
            HeapNode minNode = this.min;
            int minRank = minNode.getRank();
            HeapNode firstChild = minNode.getChild();
            HeapNode lastChild = firstChild.getPrev();

            firstChild.setPrev(minNode.getPrev());
            minNode.getPrev().setNext(firstChild);
            lastChild.setNext(minNode.getNext());
            minNode.getNext().setPrev(lastChild);

            //update numMarked+null the parents
            nullParents_Mark(firstChild);

            //update first
            if (min == first) {
                this.first = firstChild;
            }
            //update numoftrees
            numOfTrees = numOfTrees + (minRank - 1);

        }
        }



        this.successive_linking();
        //update min
            int treeNum=this.numOfTrees;
            HeapNode newMin=this.first;
            HeapNode curr=this.first;
            for (int i=0;i<treeNum;i++){
                if(curr.getKey()<newMin.getKey()){
                    newMin=curr;
                }
                curr=curr.getNext();
            }
            this.min=newMin;

    }
     	
    }

    /**
     * public void nullParents_Mark(HeapNode node)
     *
     * make the field parent for node and its siblings null
     * and makes sure the node and its siblings not marked
     * and update the field numOfMarked.
     * returns null.
     *
     *
     * O(node.getParent().getRank())
     */
    public void nullParents_Mark(HeapNode node){
        HeapNode copy=node;
        while (node.getParent()!=null||node!=copy){
            node.setParent(null);

            if(node.getMarked()){
                node.setMarked(false);
                numOfMarked--;
            }
            node=node.getNext();
        }

    }


    /**
     * public void link(HeapNode x,HeapNode y)
     *
     * links between the trees that the Roots of them are x and y
     * and makes them one tree and the Root is the minimal node in the new tree.
     * returns null.
     *
     * O(1)
     */
    public void link(HeapNode x,HeapNode y){
        totalLinks++;

        HeapNode son;
        HeapNode parent;
        //son is the bigger node
        if(x.getKey()>y.getKey()){
            son=x;
            parent=y;
        }
        else {
            son=y;
            parent=x;
        }
        //if son is first
        if(son==this.getFirst()){
            this.first=son.getNext();
        }
        son.getPrev().setNext(son.getNext());
        son.getNext().setPrev(son.getPrev());

        if(parent.getChild()==null){
            son.setPrev(son);
            son.setNext(son);

        }
        else {
            parent.getChild().getPrev().setNext(son);
            son.setPrev(parent.getChild().getPrev());
            son.setNext(parent.getChild());
            parent.getChild().setPrev(son);

        }
        son.setParent(parent);
        parent.setChild(son);

        parent.setRank(parent.getRank()+1);
        numOfTrees=numOfTrees-1;

    }


    /**
     * public void successive_linking()
     *
     * make the heap a binomial heap that contains O(logn+1) trees, at most one of each rank.
     * the trees in the heap become ordered by the ranks of the trees (small-->big).
     *
     * O(n) Worst Case
     * O(logn) Amortized
     */
    public void successive_linking(){
        double GoldenRatio=(1+Math.sqrt(5))/2;
        int maxtrees= 1+((int) (Math.log(this.size())/Math.log(GoldenRatio)));
        HeapNode[] arr_heap=new HeapNode[maxtrees];

        HeapNode curr=this.getFirst();
        int numTrees=numOfTrees;
        for(int i=0;i<numTrees;i++){
           // System.out.println(Arrays.toString(arr_heap));
            HeapNode currNext=curr.getNext();
            if(arr_heap[curr.getRank()]==null){
                arr_heap[curr.getRank()]=curr;
            }
            else {
                HeapNode linkNode=arr_heap[curr.getRank()];

                link(curr,linkNode);

                HeapNode son;
                HeapNode parent;
                if (curr.getKey() > linkNode.getKey()){
                    parent=linkNode;
                    son=curr;
                }
                else {
                    parent=curr;
                    son=linkNode;
                }
                arr_heap[son.getRank()]=null;


                while (arr_heap[parent.getRank()]!=null) {
                    int Rank1=parent.getRank();

                    link(parent,arr_heap[Rank1]);
                    if (parent.getKey() > arr_heap[Rank1].getKey()){
                        parent=arr_heap[Rank1];
                        son=parent;
                    }
                    else {
                        son=arr_heap[Rank1];
                    }
                    arr_heap[Rank1]=null;

                 }
                arr_heap[parent.getRank()]=parent;
            }
            curr=currNext;

        }


        //sort the heap from small to big
        FibonacciHeap helpHeap=new FibonacciHeap();
        HeapNode first=null;
        for (int i=0;i<arr_heap.length;i++){
            if(arr_heap[i]!=null){
                first=arr_heap[i];
                break;
            }
        }
        helpHeap.first=first;
        for (HeapNode node:arr_heap){
            if(node!=null){
                node.setNext(first);
                node.setPrev(first.getPrev());
                first.getPrev().setNext(node);
                first.setPrev(node);


            }
        }
        this.first=first;

    }


   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    * O(1)
    */
    public HeapNode findMin()
    {
        if(isEmpty()){
            return null;
        }
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    * O(1)
    */
    public void meld (FibonacciHeap heap2)
    {
        HeapNode last1=this.getFirst().getPrev();
        HeapNode last2=heap2.getFirst().getPrev();

        heap2.getFirst().setPrev(last1);
        last1.setNext(heap2.getFirst());
        this.getFirst().setPrev(last2);
        last2.setNext(this.getFirst());

        if(this.min.getKey()>heap2.min.getKey()){
            this.min=heap2.min;
        }

        numOfTrees=this.numOfTrees+heap2.numOfTrees;
        numOfMarked=this.numOfMarked+heap2.numOfMarked;
        size=this.size()+ heap2.size();


    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *
    *   O(1)
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     * O(n)
    */
    public int[] countersRep()
    {
        int maxRank=0;
        HeapNode curr=first;
        if(this.size==0){
            return new int[0];
        }
        //find the max rank
        for(int i=0;i<numOfTrees;i++){
            if(curr.getRank()>maxRank){
                maxRank=curr.getRank();
            }
            curr=curr.getNext();
        }
        //fill the array
    	int[] arr = new int[maxRank+1];
        HeapNode current=first;
        for(int i=0;i<numOfTrees;i++){
            arr[current.getRank()]=arr[current.getRank()]+1;
            current=current.getNext();

        }

        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    * O(n) Worst Case
    * O(logn) Amortize
    */
    public void delete(HeapNode x) 
    {
        decreaseKey(x,x.getKey()-this.findMin().getKey()+1);
        deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    *
    * O(n) Worst Case
    * O(1) amortized
    */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.setKey(x.getKey()-delta);
        //the heap is legal
        if(x.getParent()==null){
            if(x.getKey()<this.findMin().getKey()){
                this.min=x;
            }
            return;
        }
        else {
            if(x.getParent().getKey()<x.getKey()){
                return;
            }
            //heap is illegal
            else {
                cascading_cut(x,x.getParent());
            }
        }
        if(x.getKey()<this.findMin().getKey()){
            this.min=x;
        }
    }


    /**
     * public void cut(HeapNode son,HeapNode parent)
     *
     * cut the edge between parent and son and add the tree that its Root is son at the start(left).
     * returns null.
     *
     *O(1)
     */
    public void cut(HeapNode son,HeapNode parent){
        totalCuts++;
        son.setParent(null);
        if(son.getMarked()){
            numOfMarked--;
        }
        son.setMarked(false);
        parent.setRank(parent.getRank()-1);
        if(son.getNext()==son){
            parent.setChild(null);
        }
        else {
            parent.setChild(son.getNext());
            son.getPrev().setNext(son.getNext());
            son.getNext().setPrev(son.getPrev());
        }
        //adding son to the left

        son.setPrev(first.getPrev());
        son.getPrev().setNext(son);
        first.setPrev(son);
        son.setNext(first);

        first = son;
        numOfTrees = numOfTrees + 1;

        //update min
        if(this.min.getKey()>son.getKey()){
            min=son;
        }

    }

    /**
     * public void cascading_cut(HeapNode son,HeapNode parent)
     *
     * cut the edge between son and parent and adding son at the start while saving the invariant.
     * Invariant: Each node loses at most one child after becoming a child itself.
     * returns null.
     *
     *O(n)
     */

    public void cascading_cut(HeapNode son,HeapNode parent){
        cut(son,parent);
        if(parent.getParent()!=null){
            if(!parent.getMarked()){
                parent.setMarked(true);
                numOfMarked++;
            }
            else {
                cascading_cut(parent,parent.getParent());
            }
        }
    }


   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    *
    * O(1)
    */
    public int potential() 
    {    
    	return numOfTrees+ (2*numOfMarked);
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    *
    * O(1)
    */
    public static int totalLinks()
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
    *
    * O(1)
    */
    public static int totalCuts()
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H.
      *
      * O(k*deg(H))
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] res= new int[k-1];
        FibonacciHeap Help= new FibonacciHeap();
        HeapNode[] PotentialMin = new HeapNode[H.size];
        int Last=0;
        int minIndex=0;
        HeapNode CurrMin= H.first;

        PotentialMin[0]=H.getFirst();
        Help.insert(H.first.getKey());
        HeapNode minChildren;

        for(int i=0;i<k-1;i++){
            res[i]=Help.findMin().getKey();
            Help.deleteMin();
            minChildren=CurrMin.getChild();

            for(int j=0;j< CurrMin.getRank();j++){
                Last++;
                PotentialMin[Last]=minChildren;
                Help.insert(minChildren.getKey());
                minChildren=minChildren.getNext();
            }

            PotentialMin[minIndex]=PotentialMin[Last];
            Last--;
            CurrMin=PotentialMin[0];

            //update minIndex
            for (int j=0;j<Last+1;j++){
                if(PotentialMin[j].getKey()< CurrMin.getKey()){
                    CurrMin=PotentialMin[j];
                    minIndex=j;
                }
            }
        }
        return res;
    }

    /**
     * public HeapNode getFirst()
     *
     * returns the first node(left node) of the heap.
     *
     * O(1)
     */
    public HeapNode getFirst(){
        if(!this.isEmpty()){
        return this.first;}
        return null;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
    	private int rank;
    	private boolean marked;
    	private HeapNode child;
        private HeapNode parent;
        private HeapNode next;
        private HeapNode prev;

       /**
        * public HeapNode(int key)
        *
        * Constructor that takes the key and make a new node with the key.
        * the fields prev and next aim at itself.
        *
        * O(1)
        */
    	public HeapNode(int key) {
    		this.key = key;
    		this.prev=this;
    		this.next=this;
    	}

    	//GETTERS AND SETTERS
       /**
        * public int getKey()
        *
        * returns the field key.
        * O(1)
        */
    	public int getKey() {
    		return this.key;
    	}
       /**
        * public void setKey(int key)
        *
        * set the field key to key.
        *O(1)
        */
    	public void setKey(int key) { this.key=key;}


       /**
        * public HeapNode getChild()
        *
        * returns the field child.
        * O(1)
        */
        public HeapNode getChild() {
           return this.child;
       }
       /**
        *  public void setChild(HeapNode child)
        *
        *  change the field child to child.
        *O(1)
        */
        public void setChild(HeapNode child) {this.child=child;}


       /**
        * public HeapNode getParent().
        *
        * return the field parent.
        * O(1)
        */
        public HeapNode getParent() {
           return this.parent;
       }
       /**
        * public void setParent(HeapNode parent)
        *
        * change the field parent to parent.
        * O(1)
        */
        public void setParent(HeapNode parent) {this.parent=parent;}


       /**
        * public HeapNode getNext()
        *
        * return the field next.
        * O(1)
        */
        public HeapNode getNext() {
           return this.next;
       }
       /**
        *  public void setNext(HeapNode next)
        *
        *  change the field next to next.
        *  O(1)
        */
        public void setNext(HeapNode next) {this.next=next;}


       /**
        * public HeapNode getPrev()
        *
        * return the field prev.
        * O(1)
        */
        public HeapNode getPrev() {
           return this.prev;
       }
       /**
        * public void setPrev(HeapNode prev)
        *
        * change the field prev to prev.
        * O(1)
        */
        public void setPrev(HeapNode prev) {this.prev=prev;}


       /**
        * public int getRank()
        *
        * return the field rank.
        * O(1)
        */
        public int getRank() { return this.rank;}
       /**
        * public void setRank(int rank)
        *
        * change the field rank to rank.
        * O(1)
        */
        public void setRank(int rank) { this.rank=rank;}


       /**
        * public boolean getMarked()
        *
        * return the field marked.
        * O(1)
        */
        public boolean getMarked() { return this.marked;}
       /**
        * public void setMarked(boolean bool)
        *
        * change the field marked to bool.
        * O(1)
        */
        public void setMarked(boolean bool) { this.marked=bool;}


   }

}
