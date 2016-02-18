package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 *
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    private File file;
    private TupleDesc td;
    /**
     * Constructs a heap file backed by the specified file.
     *
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        // Donesies
        this.file = f;
        this.td = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     *
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        // Donesies
        return this.file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     *
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        // Donesies
        return getFile().getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     *
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        // Donesies
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        // Donesies
        Page result = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(this.file, "r");
            raf.seek(pid.pageNumber() * BufferPool.PAGE_SIZE);
            byte[] array = new byte[BufferPool.PAGE_SIZE];
            raf.read(array);
            HeapPageId heapId= (HeapPageId)pid;
            return new HeapPage(heapId, array);
        }
        catch (IOException e)
        {

        }
        return null;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
        // Done
        try {
            PageId pid = page.getId();

            RandomAccessFile raFile = new RandomAccessFile(this.file, "rw");
            raFile.skipBytes(BufferPool.getPageSize() * pid.pageNumber());
            byte[] data = page.getPageData();
            raFile.write(data);
            raFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found - potential problem later");
            throw e;
        } catch (IOException e) {
            System.out.println("IO Error - potential problem later");
            throw e;
        }
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        // Donesies
        long numPages = this.file.length()/BufferPool.PAGE_SIZE;
        // Round up
        return (int) Math.ceil(numPages);
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
        // Done
        int pageNo;
        for (pageNo = 0; pageNo < numPages(); pageNo++) {
            PageId pid = new HeapPageId(getId(), pageNo);
            HeapPage heapPage = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
            if (heapPage.getNumEmptySlots() > 0) {
                heapPage.insertTuple(t);
                ArrayList<Page> arrayList = new ArrayList<Page>();
                arrayList.add(heapPage);
                return arrayList;
            }
        }
        HeapPage newHeapPage = new HeapPage(new HeapPageId(getId(), pageNo), HeapPage.createEmptyPageData());
        newHeapPage.insertTuple(t);

        // Append to end of file
        // OutputStream output = new BufferedOutputStream(new FileOutputStream(this.file, true));
        // output.write(newHeapPage.getPageData());
        // output.close();
        writePage(newHeapPage);

        ArrayList<Page> arrayList = new ArrayList<Page>();
        arrayList.add(newHeapPage);
        return arrayList;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
        // Done
        RecordId rid = t.getRecordId();
        PageId pid = rid.getPageId();
        if (pid == null || pid.getTableId() != getId()) {
            throw new DbException("Tuple is not a member of the file");
        }
        HeapPage heapPage = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
        heapPage.deleteTuple(t);
        ArrayList<Page> arrayList = new ArrayList<Page>();
        arrayList.add(heapPage);
        return arrayList;

    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        // Donesies
        return new FileIterator(getId(), numPages(), tid);
    }

}

