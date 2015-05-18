package ru.salauyou.table;


/**
 * Exception to be thrown when size of passed argument 
 * (map, collection or array) doesn't fit required size
 * 
 * @author Salauyou
 */
public class WrongSizeException extends IllegalArgumentException {
	
	private static final long serialVersionUID = -25081983;
	
	
	public WrongSizeException(int expectedSize, int actualSize) {
		super(String.format("Expected size: %s, actual size: %s", 
							expectedSize, actualSize));
	};
	
	
	public WrongSizeException(String format) {
		super(format);
	}
}
