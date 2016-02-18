package simpledb;

import java.io.*;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;

    private TransactionId t;
    private DbIterator child;
    private int tableid;
    private boolean inserted;
    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        // some code goes here
        // Done
        this.t = t;
        this.child = child;
        this.tableid = tableid;
        this.inserted = false;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        // Done
        return new TupleDesc(new Type[] { Type.INT_TYPE }, new String[] { "Number inserted" });
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        // Done
        super.open();
        this.child.open();
    }

    public void close() {
        // some code goes here
        // Done
        super.close();
        this.child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        // Done
        this.child.rewind();
        this.open();
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        // Done
        if (!this.inserted) {
            int insertCount = 0;
            while (this.child.hasNext()) {
                try {
                    Database.getBufferPool().insertTuple(this.t, this.tableid, this.child.next());
                    insertCount++;
                } catch (DbException e) {
                    throw e;
                } catch (IOException e) {
                    throw new DbException("IOException here");
                } catch (TransactionAbortedException e) {
                    throw e;
                }
            }
            this.inserted = true;
            TupleDesc td = this.getTupleDesc();
            Tuple result = new Tuple(td);
            result.setField(0, new IntField(insertCount));
            return result;
        }
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        // Done
        return new DbIterator[] {this.child};
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        // Done
        this.child = children[0];
    }
}
