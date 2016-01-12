package simpledb;

import java.util.NoSuchElementException;

/**
 * Created by bleonard on 1/10/16.
 */
public class FileIterator implements DbFileIterator{

    private HeapFile file;
    private TransactionId tid;

    public void FileIterator(HeapFile file, TransactionId tid) {
        this.file = file;
        this.tid = tid;
    }
    /**
     * Opens the iterator
     * @throws DbException when there are problems opening/accessing the database.
     */
    public void open() throws DbException, TransactionAbortedException;

    /** @return true if there are more tuples available. */
    public boolean hasNext()
            throws DbException, TransactionAbortedException;

    /**
     * Gets the next tuple from the operator (typically implementing by reading
     * from a child operator or an access method).
     *
     * @return The next tuple in the iterator.
     * @throws NoSuchElementException if there are no more tuples
     */
    public Tuple next()
            throws DbException, TransactionAbortedException, NoSuchElementException;

    /**
     * Resets the iterator to the start.
     * @throws DbException When rewind is unsupported.
     */
    public void rewind() throws DbException, TransactionAbortedException;

    /**
     * Closes the iterator.
     */
    public void close();
}
