package com.upenn.parsers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by cheng on 2/15/15.
 */
public class GenomicInterval implements Comparable<GenomicInterval>  {
    Long start;
    Long end;
    public GenomicInterval(Long[] coords){
        this.start = coords[0];
        this.end = coords[1];
    }

    @Override
    public int compareTo(GenomicInterval otherInterval) throws NullPointerException {
        if (otherInterval == null) {
            throw new NullPointerException();
        } else {
            if (otherInterval.start > this.start) {
                return -1;
            } else if (otherInterval.start < this.start) {
                return 1;
            } else {
                if (otherInterval.end < this.end) {
                    return 1;
                } else if (otherInterval.end > this.end) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(43, 71). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(this.start).
                append(this.end).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GenomicInterval))
            return false;
        if (obj == this)
            return true;

        GenomicInterval rhs = (GenomicInterval) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(this.start, rhs.start).
                append(this.end, rhs.end).
                isEquals();
    }
    
    public void print_me(){
        System.out.println("["+Long.toString(this.start)+":"+Long.toString(this.end)+"]");
    }
}
