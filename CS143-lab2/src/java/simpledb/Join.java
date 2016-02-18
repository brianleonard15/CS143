package simpledb;

import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor. Accepts to children to join and the predicate to join them
     * on
     * 
     * @param p
     *            The predicate to use to join the children
     * @param child1
     *            Iterator for the left(outer) relation to join
     * @param child2
     *            Iterator for the right(inner) relation to join
     */

    private DbIterator[] children;
    private JoinPredicate p;
    private Tuple currentOuterTuple = null;

    public Join(JoinPredicate p, DbIterator child1, DbIterator child2) {
        // some code goes here
        // Done
        this.children = new DbIterator[] {child1, child2};
        this.p = p;
    }

    public JoinPredicate getJoinPredicate() {
        // some code goes here
        // Done
        return this.p;
    }

    /**
     * @return
     *       the field name of join field1. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField1Name() {
        // some code goes here
        // Done
        TupleDesc td = this.children[0].getTupleDesc();
        return td.getFieldName(this.p.getField1());
    }

    /**
     * @return
     *       the field name of join field2. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField2Name() {
        // some code goes here
        // Done
        TupleDesc td = this.children[1].getTupleDesc();
        return td.getFieldName(this.p.getField2());
    }

    /**
     * @see simpledb.TupleDesc#merge(TupleDesc, TupleDesc) for possible
     *      implementation logic.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        // Done
        TupleDesc td1 = this.children[0].getTupleDesc();
        TupleDesc td2 = this.children[1].getTupleDesc();
        return TupleDesc.merge(td1, td2);
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
        // Done
        super.open();
        this.children[0].open();
        this.children[1].open();

        this.currentOuterTuple = null;
    }

    public void close() {
        // some code goes here
        super.close();
        this.children[0].close();
        this.children[1].close();

        this.currentOuterTuple = null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        // Done
        this.children[0].rewind();
        this.children[1].rewind();
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no
     * more tuples. Logically, this is the next tuple in r1 cross r2 that
     * satisfies the join predicate. There are many possible implementations;
     * the simplest is a nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of Join
     * are simply the concatenation of joining tuples from the left and right
     * relation. Therefore, if an equality predicate is used there will be two
     * copies of the join attribute in the results. (Removing such duplicate
     * columns can be done with an additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     * 
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        // Done
        Tuple t1 = this.currentOuterTuple;
        while (t1 != null && this.children[1].hasNext()) {
            Tuple t2 = this.children[1].next();
            if (this.p.filter(t1, t2)) {
                TupleDesc td1 = t1.getTupleDesc();
                TupleDesc td2 = t2.getTupleDesc();
                TupleDesc td = this.getTupleDesc();
                Tuple joinTuple = new Tuple(td);
                for (int i = 0; i < td1.numFields(); i++) {
                    joinTuple.setField(i, t1.getField(i));
                }
                for (int i = 0; i < td2.numFields(); i++) {
                    joinTuple.setField(td1.numFields() + i, t2.getField(i));
                }
                return joinTuple;
            }
        }
        if (this.children[0].hasNext()) {
            this.currentOuterTuple = this.children[0].next();
            this.children[1].rewind();
            return this.fetchNext();
        }
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        // Done
        return this.children;
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        // Done
        this.children = Arrays.copyOfRange(children, 0, 2);
    }

}
