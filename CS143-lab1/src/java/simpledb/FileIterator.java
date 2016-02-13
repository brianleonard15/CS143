package simpledb;

import java.util.*;

/**
 * Created by bleonard on 1/10/16.
 */
public class FileIterator implements DbFileIterator{
    private int tableid;
    private int numPages;
    private int currentPage;
    private TransactionId tid;
    private Iterator<Tuple> pageIterator;

    /**
     * Constructs an iterator from the specified table id and transaction
     * id
     *
     * @param tableid
     *            The tableid to iterate over
     * @param numPages
     *            The total number of pages in the HeapFile
     * @param tid
     *            The transaction id
     *
     */
    public FileIterator(int tableid, int numPages, TransactionId tid) {
        this.tableid = tableid;
        this.numPages = numPages;
        this.tid = tid;
    }

    /**
     * Gets the HeapPage's tuple iterator given a page number - this uses the
     * BufferPool
     * @throws DbException when there are problems opening/accessing the database.
     */
    private Iterator<Tuple> getPageIterator(int pageNo)
            throws DbException, TransactionAbortedException {
        PageId pid = new HeapPageId(this.tableid, pageNo);
        BufferPool bp = Database.getBufferPool();
        HeapPage page = (HeapPage)bp.getPage(this.tid, pid, Permissions.READ_ONLY);
        return page.iterator();
    }

    /**
     * Opens the iterator
     * @throws DbException when there are problems opening/accessing the database.
     */
    public void open()
            throws DbException, TransactionAbortedException {
        this.currentPage = 0;
        this.pageIterator = getPageIterator(this.currentPage);
    }

    /** @return true if there are more tuples available. */
    public boolean hasNext()
            throws DbException, TransactionAbortedException {
        if (this.pageIterator == null) {
            return false;
        }
        if (this.pageIterator.hasNext()) {
            return true;
        } else {
            while (this.currentPage < (numPages - 1)) {
                this.currentPage++;
                this.pageIterator = getPageIterator(this.currentPage);
                if (this.pageIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Gets the next tuple from the operator (typically implementing by reading
     * from a child operator or an access method).
     *
     * @return The next tuple in the iterator.
     * @throws NoSuchElementException if there are no more tuples
     */
    public Tuple next()
            throws DbException, TransactionAbortedException, NoSuchElementException {
        if (hasNext()) {
            return this.pageIterator.next();
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Resets the iterator to the start.
     * @throws DbException When rewind is unsupported.
     */
    public void rewind()
            throws DbException, TransactionAbortedException {
        close();
        open();
    }

    /**
     * Closes the iterator.
     */
    public void close() {
        this.currentPage = 0;
        this.pageIterator = null;
    }
}
