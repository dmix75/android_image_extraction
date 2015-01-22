package org.sda.block.image;

public class RangeSet {
	private int[] rangeSet;
	private int count;
	public RangeSet(int count, int[] rangeSet) {
		super();
		this.rangeSet = rangeSet;
		this.count = count;
	}
	public int[] getRangeSet() {
		return rangeSet;
	}
	public int getCount() {
		return count;
	}
}

