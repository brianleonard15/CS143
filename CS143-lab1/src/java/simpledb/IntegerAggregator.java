package simpledb;
import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    // Done
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;

    private int count = 0;
    private int sum = 0;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    private HashMap<Field, Integer> group;
    private HashMap<Field, Integer> countGroup;

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        // Done
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.what = what;

        this.group = new HashMap<Field, Integer>();
        this.countGroup = new HashMap<Field, Integer>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
        // Done
        Field aggregateField = tup.getField(this.afield);
        int fieldValue = ((IntField)aggregateField).getValue();
        if (this.gbfield == NO_GROUPING) {
            switch (this.what) {
                case MIN:
                    this.min = fieldValue < this.min ? fieldValue : this.min;
                    break;
                case MAX:
                    this.max = fieldValue > this.max ? fieldValue : this.max;
                    break;
                case SUM:
                    this.sum += fieldValue;
                    break;
                case AVG:
                    this.count += 1;
                    this.sum += fieldValue;
                    break;
                case COUNT:
                    this.count += 1;
                    break;
            }
        } else {
            Field keyField = tup.getField(this.gbfield);
            if (!this.group.containsKey(keyField)) {
                defaultValueForHashWithKey(this.group, keyField);
            }
            int curVal = this.group.get(keyField);
            switch (this.what) {
                case MIN:
                    curVal = fieldValue < curVal ? fieldValue : curVal;
                    break;
                case MAX:
                    curVal = fieldValue > curVal ? fieldValue : curVal;
                    break;
                case SUM:
                    curVal += fieldValue;
                    break;
                case AVG:
                    if (!this.countGroup.containsKey(keyField)) {
                        defaultValueForHashWithKey(this.countGroup, keyField);
                    }
                    int countVal = this.countGroup.get(keyField);
                    this.countGroup.put(keyField, countVal + 1);
                    curVal += fieldValue;
                    break;
                case COUNT:
                    curVal += 1;
                    break;
            }
            this.group.put(keyField, curVal);
        }
    }

    private void defaultValueForHashWithKey(HashMap<Field, Integer> map, Field field) {
        switch (this.what) {
            case MIN:
                map.put(field, this.min);
                break;
            case MAX:
                map.put(field, this.max);
                break;
            case SUM:
                map.put(field, this.sum);
                break;
            case AVG:
                map.put(field, this.count);
                break;
            case COUNT:
                map.put(field, this.count);
                break;
        }
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        // Done
        ArrayList<Tuple> tupleList;
        if (this.gbfield != NO_GROUPING) {
            tupleList = new ArrayList<Tuple>(this.group.size());
            Type[] typeAr = new Type[] {this.gbfieldtype, Type.INT_TYPE};
            String[] stringAr = new String[] {"GROUP", this.what.toString()};
            TupleDesc td = new TupleDesc(typeAr, stringAr);
            for (Field field : this.group.keySet()) {
                Tuple tup = new Tuple(td);
                tup.setField(0, field);
                int aggregateVal = this.group.get(field);
                IntField aggregateIntField;
                if (this.what == Op.AVG) {
                    int countVal = this.countGroup.get(field);
                    aggregateIntField = new IntField(aggregateVal/countVal);
                    tup.setField(1, aggregateIntField);
                } else {
                    aggregateIntField = new IntField(aggregateVal);
                    tup.setField(1, aggregateIntField);
                }
                tupleList.add(tup);
            }
            TupleIterator it = new TupleIterator(td, tupleList);
            return it;
        } else {
            tupleList = new ArrayList<Tuple>(1);
            Type[] typeAr = new Type[] {Type.INT_TYPE};
            String[] stringAr = new String[] {this.what.toString()};
            TupleDesc td = new TupleDesc(typeAr, stringAr);
            Tuple tup = new Tuple(td);
            int aggregateVal = 0;
            switch (this.what) {
                case MIN:
                    aggregateVal = this.min;
                    break;
                case MAX:
                    aggregateVal = this.max;
                    break;
                case SUM:
                    aggregateVal = this.sum;
                    break;
                case AVG:
                    aggregateVal = this.sum/this.count;
                    break;
                case COUNT:
                    aggregateVal = this.count;
                    break;
            }
            IntField aggregateIntField = new IntField(aggregateVal);
            tup.setField(0, aggregateIntField);

            tupleList.add(tup);
            TupleIterator it = new TupleIterator(td, tupleList);
            return it;
        }
    }

}
