package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;

    private TransactionId t;
    private DbIterator child;
    private boolean deleted;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
        // Done
        this.t = t;
        this.child = child;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        // Done
        return new TupleDesc(new Type[] { Type.INT_TYPE }, new String[] { "Number deleted" });
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
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        // Done
        if (!this.deleted) {
            int deleteCount = 0;
            while (this.child.hasNext()) {
                try {
                    Database.getBufferPool().deleteTuple(this.t, this.child.next());
                    deleteCount++;
                } catch (DbException e) {
                    throw e;
                } catch (IOException e) {
                    throw new DbException("IOException here");
                } catch (TransactionAbortedException e) {
                    throw e;
                }
            }
            this.deleted = true;
            TupleDesc td = this.getTupleDesc();
            Tuple result = new Tuple(td);
            result.setField(0, new IntField(deleteCount));
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
