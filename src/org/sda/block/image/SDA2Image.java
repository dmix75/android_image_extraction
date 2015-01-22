package org.sda.block.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class SDA2Image {

	/** DEFINE BLOCK_SIZE */
	private static final int BLOCK_SIZE = 4096;

	/** turn on debugging */
	private boolean debug = false;

	/** Transfer List File */
	private File fTransferList;

	/** System Data Files system.new.dat */
	private File fDataFile;

	/** Output System image name */
	private File fOutFile;

	/** Total Blocks to write */
	private long totalBlocks;

	/** Version */
	private int version;

	/** New Block values */
	private RangeSet newBlockSet;

	/** Erase Block values */
	private RangeSet eraseBlockSet;


	public SDA2Image(String transferList, String dataFile, String outFile) throws IOException {

		this.fTransferList = new File(transferList);
		this.fDataFile = new File(dataFile);
		this.fOutFile = new File(outFile);

		if ( ! fTransferList.exists() ) {
			throw new IllegalArgumentException("File \"" + transferList + " does not exist!");
		}

		if ( ! fDataFile.exists() ) {
			throw new IllegalArgumentException("File \"" + dataFile + " does not exist!");			
		}

		parseTransferListFile();
	}

	/**
	 * Build the Range set
	 * @param rangeList
	 * @param set
	 */
	private RangeSet setRange(String rangeList) {

		// -- 1st item is count
		// -- everything else is range items

		String blocks[] = rangeList.split(",");
		int count = Integer.valueOf(blocks[0]);
		int rangeSet[] = new int[count];
		for ( int i = 0; i < count; i++ ) {
			rangeSet[i] = Integer.valueOf(blocks[i + 1]);
		}
		return new RangeSet(count / 2, rangeSet);
	}

	/**
	 * Parse the transfer List file
	 */
	private void parseTransferListFile() throws IOException {

		List<String> lines = Files.readAllLines(fTransferList.toPath(), Charset.defaultCharset());

		// -- Line 1 ==> Version
		version = Integer.valueOf(lines.get(0));

		// -- Line 2 ==> Blocks to write
		totalBlocks = Long.valueOf(lines.get(1));

		// -- Next lines are commands.
		// -- We only care about two commands for new builds.  (erase, new)
		for ( int i = 2; i < lines.size(); i++ ) {

			String line = lines.get(i);
			String cmd [];
			if ( line.startsWith("erase") ) {

				cmd = line.split(" ");
				eraseBlockSet = setRange(cmd[1]);

				debug("Erase Set");
				debugRangeSet(eraseBlockSet);
			} else if ( line.startsWith("new") ) {

				cmd = line.split(" ");
				newBlockSet = setRange(cmd[1]);

				debug("New Set");
				debugRangeSet(newBlockSet);

			} else {
				debug(line);
			}

		}
	}

	/**
	 * Print out Range values
	 * @param set
	 */
	private void debugRangeSet(RangeSet set) {

		if ( debug ) {
			System.out.println("Range Count: " + set.getCount());
			for ( int i : set.getRangeSet() ) {
				System.out.println("\t"+i);
			}
		}
	}
	/**
	 * Print debug messages
	 * @param message
	 */
	private void debug(String message) {
		if ( debug ) {
			System.out.println(message);
		}
	}

	/**
	 * Execute erase command by writing zero's for blocks
	 * @throws IOException 
	 */
	private void eraseImage() throws IOException {

		System.out.println("Initialzing Image: " + fOutFile.getName() + " ...");
		RandomAccessFile out = null;
		FileChannel channel = null;
		try {

			out = new RandomAccessFile(fOutFile, "rw");
			channel = out.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);

			for ( int i = 0; i < eraseBlockSet.getCount(); i++ ) {

				// -- Seek to position in file
				channel.position(eraseBlockSet.getRangeSet()[i*2] * BLOCK_SIZE);

				for ( int j = eraseBlockSet.getRangeSet()[i*2]; 
						j < eraseBlockSet.getRangeSet()[i*2+1]; j++) {

					// -- Write Zeros for each block
					//System.out.println("Erasing block: " + j);
					channel.write(buffer);

					// -- Reset our byte buffer
					buffer.flip();
				}
			}
		} finally {
			if ( null != channel ) channel.close();
			if ( null != out ) out.close();
		}

	}

	/**
	 * Write blocks from image to new image
	 * @throws IOException 
	 */
	public void writeBlocks() throws IOException {

		System.out.println("Building Image...");
		FileChannel channelIn = null;
		FileChannel channelOut = null;
		RandomAccessFile randomDataOut = null;
		FileInputStream imageFileIn = null;

		try {
			randomDataOut = new RandomAccessFile(fOutFile,"rw");
			imageFileIn = new FileInputStream(fDataFile);
			channelIn = imageFileIn.getChannel();
			channelOut = randomDataOut.getChannel();
			
			for ( int i = 0; i < newBlockSet.getCount(); i++ ) {

				int begin = newBlockSet.getRangeSet()[i*2];
				int end = newBlockSet.getRangeSet()[i*2+1];
				int block_count = end - begin;

				// -- Read in blocks
				System.out.print("Reading " + block_count + " blocks... ");
				ByteBuffer buffer = ByteBuffer.allocate(block_count*BLOCK_SIZE);
				channelIn.read(buffer);

				// -- Write out blocks
				System.out.print("Writing to " + begin + "... ");
				channelOut.position(begin*BLOCK_SIZE);
				buffer.flip();
				channelOut.write(buffer);
				System.out.println("Done!");
			}
		} finally {
			if ( null != channelIn ) channelIn.close();
			if ( null != channelOut ) channelOut.close();
			if ( null != randomDataOut ) randomDataOut.close();
			if ( null != imageFileIn ) imageFileIn.close();
		}

	}

	/**
	 * Extract the image 
	 * @throws IOException 
	 */
	public void extractImage() throws IOException {

		// -- Erase Image first
		eraseImage();

		// -- Write new Blocks
		writeBlocks();
	}

	public static void showUsage() {
		System.out.println("SDA2Image - usage: \n\nSDA2Image <transfer_list_file> <system_new_file> <system_img>");
		System.exit(-1);
	}

	public static void main(String args[]) throws IOException {

		// -- Check arguments for 3 items
		if ( 3 != args.length) showUsage();

		// -- Initialize SDA2Image
		SDA2Image extractor = new SDA2Image(args[0], args[1], args[2]);

		// -- Extract it
		extractor.extractImage();

	}
}
