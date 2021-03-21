package rlist;
import java.lang.Math;

class Point {
	public double x, y;
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double dist(Point p) {
		return Math.sqrt(
			(this.x-p.x)*(this.x-p.x)
			+(this.y-p.y)*(this.y-p.y)
		);
	}
	public Point copy() {
		return new Point(this.x, this.y);
	}
}

class Record {
	public int id;
	public Point location;
	public double score;

	public Record(int id, Point location, double score) {
		this.id = id;
		this.location = location;
		this.score = score;
	}

	public Record copy() {
		return new Record(
			this.id, 
			this.location.copy(), 
			this.score
		);
	}
}

class Node {
	public Record poi;
	public Node next;

	public Node(Record poi) {
		this.poi = poi;
	}
}

class RankList {
	
	private Node first;
	private int nodeCount;

	public RankList(){ 
		first=null;
		nodeCount=0;
	}
    
	public int size(){ return nodeCount; }

	public int insert(Record poi) {
		Node insertedNode=new Node(poi);
		//nodeCount==0 Η λίστα είναι κενή.
		if(first==null) {
		  first=insertedNode;
		}
		else{ //Η λίστα δεν είναι κενή.
		
			Node current,previous; 
			current=first;
			previous=null;
			
			// Τοποθετώ τον καινούργιο κόμβο στην αρχή της λίστας. 
			// Με αυτό τον τρόπο θα δημιουργήσω μια σορταρισμένη λίστα με βάση το score του κάθε poi σε φθήνουσα σειρα.
			// Αυτό θα με βοηθήσει αργότερα στις μεθόδους highScore. 

			//Αυτός ο βρόχος θα μου βρεί σε πιο σημείο της λίστας θα γίνει η εισαγωγη.
			while(current!=null){
				
				if(current.poi.score<poi.score) break;
			    previous=current;		 
			    current=current.next;
			}
			
			// Ο καινούργιος κόμβος μπαίνει στην αρχή της λίστας.
			if(previous==null){ 
			    insertedNode.next=first;
			    first=insertedNode;
			}
			// Ο καινούργιος κόμβος μπαίνει κάπου ενδιάμεσα στης λίστα.
			else{
				insertedNode.next=current;
				previous.next=insertedNode;
			}
			
		}
		nodeCount++;  // Αυξάνει το πλήθος των κόμβων κατά 1 
		return nodeCount;
	}
    
	
	public void display(){ // Μέθοδος που εμφανίζει τα περιεχόμενα μιας λίστας. //TO REMOVE
    
       Node current;
       if(first==null){
    	   System.out.println("List is empty!");
       }
       else{
    	   current=first;
    	   while(current!=null){
    		   System.out.println("Id:"+current.poi.id+" Score:"+current.poi.score+" Location:"+current.poi.location.x+","+current.poi.location.y);
    	       current=current.next;
    	   }
       }
    }
	
	
	public RankList nearest(Point p, int k) {
		RankList result_list=new RankList();
		RankList distances = new RankList();
		Node current=first;
		
		while(current!=null){
			
			//Η μεταβλητή distance παίρνει τις αποστάσεις κάθε άλλου poi από αυτό το οποίο δώθηκε ως όρισμα στη κλήση της nearest.
			double distance=p.dist(current.poi.location); 
			
			// Δημιουργώ records ξαναχρησιμοποιώντας τον consrtuctor της Record(id,location,score) όμως αυτή τη φορά αντι για score εισάγω τη μεταβλητή distance που είναι τύπου double...
			Record rec4dist=new Record(current.poi.id,current.poi.location,distance); 			
			//...και τα εισάγω σε αντικείμενο κόμβου
			Node newNode=new Node(rec4dist);
			
			//αν η λιστα είναι κενή, ο πρώτος κόμβος μπαίνει στην αρχή.
			if(distances.first==null){ 
				distances.first=newNode;	
			}
			else{
			   //τοποθέτηση νέων κόμβων με βάση την αποσταση. 
				Node dc,dp; //dc: distancesCurrent αναφορά. Ομοίως dp: distancesPrevious.
				dp=null;
				dc=distances.first;
				
				while(dc!=null){
				   if(dc.poi.score>newNode.poi.score) break;
				   dp=dc;
				   dc=dc.next;
				}
				if(dp==null){
					newNode.next=distances.first;
					distances.first=newNode;
				}
				else{
					newNode.next=dc;
					dp.next=newNode;
				}
			}
			current=current.next;
		}
		// Αν ο αριθμός εγγραφών που ζητηθεί είναι μεγαλύτερος από τον αριθμό υπαρχόντων εγγραφών, 
		//ή αλλιώς, αν δεν έχω αρκετά στοιχεία όσα μου ζητούνται, τότε παίρνω όλες τις εγγραφές που έχω και τις εμφανίζω...

		if (nodeCount<=k){ 			
			current=first;
			while(current!=null){
				Record rec = current.poi.copy();
				result_list.insert(rec);
				current = current.next;
			}	
		}
		
	//...αλλιώς εμφανίζω τις k πρώτες εγγραφές ψάχνοντας με βάση ομοια id στη λίστα των αποστάσεων και αυτή που έχω ήδη. 
		
		else{
			Node distances_pace = distances.first;
			
			for (int r=0; r<k; r++){
				current = first;
				int code = distances_pace.poi.id;
				
				//Οταν βρίσκω ίδια id τότε εισάγω ένα αντίγραφο του κόμβου με το εκάστοτε id στη λίστα result_list και την επιστρέφω.
				while(current!=null){
					if (current.poi.id==code){
						Record rec = current.poi.copy();
						result_list.insert(rec);
						break;
					}
					current=current.next;
				}
				distances_pace=distances_pace.next;
			}
		}
		return result_list;
	}
	// Επιστρέφει λίστα με όλους τα σημεία ενδιαφέροντος που απέχουν το πολύ maxDist απόσταση από το δοθέν σημείο p. 
	// Θεωρώ target_distance μια μεταβλητή όπου αποθηκεύω κάθε φορά την απόσταση του εκάστοτε σημείου απο το p.
	// Διατρέχω όλη τη λίστα με τα σημεία ενδιαφέροντος που έχω και συγκρίνω τις αποστάσεις τους στη μεταβλητή target_distance με το maxDist.
	// Αν η απόσταση που είναι αποθηκευμένη στη target_distance είναι μικρότερη ή ίση της maxDist, 
	// τότε αποθηκεύω το αντίγραφο του εκάστοτε κόμβου στη λίστα result_list και την επιστρέφουμε.
	public RankList nearest(Point p, double maxDist) {
		Node cur = first;
		RankList result_list = new RankList();
		
		while(cur!=null) {
			double target_distance = p.dist(cur.poi.location);
			
			if (target_distance <= maxDist) {
				Record rec = cur.poi.copy();
				result_list.insert(rec);
			}
			
			cur=cur.next;
		}
		return result_list;
	}
	// Επιστρέφει λίστα με τους k κόμβους με το μεγαλύτερο Score.
	// Αρχικά ελέγχω αν ο αριθμός των κόμβων που ζητούνται να εμφανιστούν είναι μεγαλύτερος από τον αριθμό κόμβων που έχει η λίστα στην οποία καλέιται η μέθοδος.
	// Αν ναι, τότε επιστρέφω όλα τα στοιχεία της λίστας καθώς όλα πληρούν το κριτήριο. Αν όχι, τότε εμφανίζω τα k πρώτα. 
	// Εδώ βοηθούμαστε από την δομή της insert καθώς όπως περιγράφεται παραπάνω, η λίστα που δημιουργείται κατά την εισαγωγή είναι ήδη ταξινομημένη κατά score με φθήνουσα σειρα.
	// Σε κάθε περίπτωση αποθηκεύουμε το αντίγραφο του εκάστοτε κόμβου στη λίστα result_list και την επιστρέφουμε.
	public RankList highScore(int k) {
		Node cur1 = first;
		RankList result_list = new RankList();
		
		if(nodeCount<=k) {
			while(cur1!=null) {
				Record rec = cur1.poi.copy();
				result_list.insert(rec);
				cur1 = cur1.next;
			}
			
		}
		else {
			for(int cnt=0; cnt<k; cnt++) {
				Record rec = cur1.poi.copy();
				result_list.insert(rec);
				cur1 = cur1.next;
			}
		}
		
		
		return result_list;
	}
	// Επιστρέφει λίστα με όλους τους κόμβους που έχουν score μεγαλύτερο ή ίσο με minScore.
	// Διατρέχοντας τη λίστα στην οποία καλείται η μέθοδος αν βρούμε κόμβο με score ίσο ή μεγαλύτερο από το minScore 
	// τότε αποθηκεύουμε το αντίγραφο του εκάστοτε κόμβου στη λίστα result_list και την επιστρέφουμε.
	public RankList highScore(double minScore) {
		Node cur = first;
		RankList result_list = new RankList();
		
			while(cur!=null) {
				
				if(cur.poi.score>=minScore) {
					Record rec = cur.poi.copy();
					result_list.insert(rec);
				}
				
				cur = cur.next;
			}
		
		return result_list;
	}

	public RankList inCommonWith(RankList rankList) {
		RankList result_list = new RankList();
		Node cur1 = first;
		Node cur2 = null;
		//Ψάχνω διατρέχοντας τη λίστα στην οποία καλείταιη inCommonWith και τη λίστα rankList (όρισμά της).
		//Οταν βρίσκω ίδια id τότε εισάγω ένα αντίγραφο του κόμβου με το εκάστοτε id στη λίστα result_list και την επιστρέφω.

		while(cur1!=null) {
			cur2 = rankList.first;
			while(cur2!=null) {
				if(cur1.poi.id==cur2.poi.id){
					Record rec = cur1.poi.copy();
					result_list.insert(rec);
					break;
				}
				cur2=cur2.next;
			}
			cur1=cur1.next;
		}
		return result_list;
	}
}
















