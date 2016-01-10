package simpledb;

import java.util.*;

/**
 * Created by bleonard on 1/10/16.
 */
public class PageIterator implements Iterator<Tuple> {

    private HeapPage page;
    private int index;

    public PageIterator(HeapPage heapPage) {
        this.page = heapPage;
        this.index = 0;
    }

    public boolean hasNext() {
        if ((this.index < this.page.tuples.length) && (this.page.tuples[index] != null)) {
            return true;
        }
        else {
            return false;
        }
    }

    public Tuple next() {
        if (!hasNext())
            throw new NullPointerException();
        return this.page.tuples[this.index++];
    }

    public void remove() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }
}
