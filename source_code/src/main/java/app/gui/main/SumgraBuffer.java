package app.gui.main;

import org.apache.log4j.Logger;

import com.gs.collections.impl.list.mutable.FastList;

import app.gui.database.GuiGraphDB;

/**
 * @author Erick Cuenca
 *
 */
public class SumgraBuffer {

	public Logger logger = Logger.getLogger(SumgraBuffer.class);
	public static GuiGraphDB processingApp;

	private FastList<int[]> embeddings = new FastList<int[]>();
	private int bufferThreshold;
	private boolean available;

	/**
	 * Default constructor
	 * 
	 * @param bufferThreshold
	 *            limit of the embedding list to fetch results
	 * 
	 */
	public SumgraBuffer(int bufferThreshold) {
		this.bufferThreshold = bufferThreshold;
	}

	/**
	 * Method to fill the embedding list from SuMGra results
	 * 
	 * @param embedding
	 */
	public synchronized void putOn(int[] embedding) {
		while (available) {
			try {
				// wait while embedding is full
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.embeddings.add(embedding);
		if (embeddings.size() == bufferThreshold) {
			available = true;
			notify();
		}
	}

	/**
	 * Method to pickUp the embedding list and update the listNodePatterns
	 */
	public synchronized void pickUp() {
		while (!available) {
			try {
				// wait while embedding empty (available=false)
				// wait while embedding is not size limit
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// CALL SYNCHRONIZED
		processingApp.fetchEmbeddings(embeddings);

		this.embeddings.clear();
		if ((this.embeddings.size() == 0)) {
			available = false;
			notify();
		}
	}

	/**
	 * Method to load the remaining embedding when the process is finish
	 */
	public synchronized void finishProcess() {	
		GuiGraphDB.sumgraProcessState = Constants.SUMGRA_STATE_FINISHED;
		processingApp.fetchEmbeddings(embeddings);
		processingApp.pausedSumgraProcess();
		processingApp.finishedSumgraProcess();
		this.embeddings.clear();
	}

	public FastList<int[]> getListEmbeddings() {
		return embeddings;
	}

	public void setListEmbeddings(FastList<int[]> listEmbeddings) {
		this.embeddings = listEmbeddings;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getListThreshold() {
		return bufferThreshold;
	}

	public void setListThreshold(int listThreshold) {
		this.bufferThreshold = listThreshold;
	}

}
