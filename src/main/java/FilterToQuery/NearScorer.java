package FilterToQuery;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 15:05  2017/9/5
 * @Modified By:
 */
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity.SimScorer;

import java.io.IOException;

public final class NearScorer extends Scorer {
   private final DocIdSetIterator DocIdSetIterator;
   private final SimScorer docScorer;

  public NearScorer(Weight weight, DocIdSetIterator td, SimScorer docScorer) {
       super(weight);
       this.docScorer = docScorer;
       this.DocIdSetIterator = td;
   }

   public int docID() {
       return this.DocIdSetIterator.docID();
   }

   public int freq() throws IOException {
       return 0;
   }

   public DocIdSetIterator iterator() {
       return this.DocIdSetIterator;
   }

   public float score() throws IOException {
       assert this.docID() != 2147483647;

       return this.docScorer.score(this.DocIdSetIterator.docID(), (float)0);
   }

   public String toString() {
       return "scorer(" + this.weight + ")[" + super.toString() + "]";
   }
}

