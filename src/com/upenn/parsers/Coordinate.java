package com.upenn.parsers;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by cheng on 2/13/15.
 */
public class Coordinate implements Comparable<Coordinate> {
    long coord;
    boolean end;

    public Coordinate(long coord, boolean end) {
        this.coord = coord;
        this.end = end;
    }

    public String print_coord(){
        return Long.toString(this.coord)+":"+ ((end) ? "end" : "start");
    }
    
    @Override
    public int compareTo(Coordinate otherCoordinate) throws NullPointerException {
        if (otherCoordinate == null) {
            throw new NullPointerException();
        } else {
            if (otherCoordinate.coord > this.coord) {
                return -1;
            } else if (otherCoordinate.coord < this.coord) {
                return 1;
            } else {
                if (otherCoordinate.end == this.end) {
                    return 0;
                } else if (otherCoordinate.end == true & this.end == false) return -1;
                else {
                    return 1;
                }
            }
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(this.coord).
                append(this.end).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Coordinate))
            return false;
        if (obj == this)
            return true;

        Coordinate rhs = (Coordinate) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(this.coord, rhs.coord).
                append(this.end, rhs.end).
                isEquals();
    }
    
}

