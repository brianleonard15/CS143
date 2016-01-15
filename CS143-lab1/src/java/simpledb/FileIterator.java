package simpledb;

import java.util.*;

/**
 * Created by bleonard on 1/10/16.
 */
public class FileIterator implements DbFileIterator{

    private HeapFile file;
    private TransactionId tid;
    private int i;
    private Iterator<Tuple> iterator;

    public FileIterator(HeapFile file, TransactionId tid) {
        this.file = file;
        this.tid = tid;
        this.i = 0;
    }
    /**
     * Opens the iterator
     * @throws DbException when there are problems opening/accessing the database.
     */
    public void open() throws DbException, TransactionAbortedException {
        PageId pageId = new HeapPageId(this.file.getId(), i);
        HeapPage heapPage = (HeapPage)Database.getBufferPool().getPage(this.tid, pageId, Permissions.READ_ONLY);
        this.iterator = heapPage.iterator();
    }

    /** @return true if there are more tuples available. */
    public boolean hasNext() throws DbException, TransactionAbortedException {
        return (this.iterator == null || !this.iterator.hasNext() || this.i >= this.file.numPages()) ? false : true;
    }

    /**
     * Gets the next tuple from the operator (typically implementing by reading
     * from a child operator or an access method).
     *
     * @return The next tuple in the iterator.
     * @throws NoSuchElementException if there are no more tuples
     */
    public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (this.iterator.hasNext()) return this.iterator.next();
        open();
        return this.iterator.next();
    }

    /**
     * Resets the iterator to the start.
     * @throws DbException When rewind is unsupported.
     */
    public void rewind() throws DbException, TransactionAbortedException {
        this.i = 0;
        open();
    }

    /**
     * Closes the iterator.
     */
    public void close() {
        this.iterator = null;
        this.i = 0;
    }
}
