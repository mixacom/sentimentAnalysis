package classification;

public class SplitCondition {
	private final double lower;
	private final double upper;
	private final double error;
	
	public SplitCondition(double lower, double upper, int[][] matrix) {
		this.lower = lower;
		this.upper = upper;
		this.error = calculateWeightedAvarageImpurity(matrix); //1000 - (matrix[0][0] + matrix[1][1] + matrix[2][2]);
	}
	
	public double getLower() {
		return lower;
	}
	
	public double getUpper() {
		return upper;
	}
	
	public double getError() {
		return error;
	}
	
	public double calculateWeightedAvarageImpurity(int[][] matrix) {
		int total = getTotal(matrix);
		double error = 0;
		
		for (int i=0; i<matrix[0].length; i++) {
			int[] column = getColumn(matrix, i);
			int sumOfColumn = 0;
			for (int j=0; j<column.length; j++) {
				sumOfColumn += column[j];
			}
			error += (double)sumOfColumn/(double)total * impurity(column);
		}
		return error;
	}
	
	private double impurity(int[] values) {
		int total = 0;
		for (int i=0; i<values.length; i++) {
			total += values[i];
		}
		
		double max = 0;
		for (int i=0; i<values.length; i++) {
			double fraction = (double)values[i]/(double)total;
			if (fraction > max) {
				max = fraction;
			}
		}
		
		return 1 - max;
	}
	
	private int[] getColumn(int[][] matrix, int columnNumber) {
		int[] column = new int[matrix.length];
		for (int j=0; j<matrix[0].length; j++) {
			column[j] = matrix[j][columnNumber];
		}
		return column;
	}
	
	private int getTotal(int[][] matrix) {
		int total = 0;
		for (int i=0; i<matrix.length; i++) {
			for (int j=0; j<matrix[i].length; j++) {
				total += matrix[i][j];
			}
		}
		return total;
	}
}