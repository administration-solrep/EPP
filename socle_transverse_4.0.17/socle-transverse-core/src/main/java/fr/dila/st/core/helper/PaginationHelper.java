package fr.dila.st.core.helper;

public final class PaginationHelper {

    private PaginationHelper() {}

    public static long calculeOffSet(long offset, long pageSize, long resultsCount) {
        if (pageSize > 0) {
            int pageNumber = 1 + (int) (offset / pageSize);
            if (pageSize * pageNumber > resultsCount) {
                offset = (resultsCount / pageSize) * pageSize;
            }
        }
        return offset;
    }

    public static long calculeFromES(Integer pageNumber, Integer pageSize, Integer resultsCount) {
        long offset = (pageNumber - 1) * pageSize;
        if (resultsCount != null && pageSize * pageNumber > resultsCount) {
            offset = (resultsCount / pageSize) * pageSize;
        }
        return offset;
    }

    public static int calculePageNumber(long offset, long pageSize, long resultsCount) {
        int pageNumber = 1 + (int) (offset / pageSize);
        if (pageSize * pageNumber > resultsCount) {
            pageNumber = (int) Math.ceil((double) resultsCount / pageSize);
        }
        return pageNumber;
    }

    public static int getPageFromCurrentIndex(long currentIndex) {
        return 1 + (int) currentIndex;
    }

    public static int getPageFromOffsetAndSize(long offset, long pageSize) {
        return (int) (1 + offset / pageSize);
    }
}
