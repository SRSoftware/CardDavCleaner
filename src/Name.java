import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Name {
	
	
	
	private String last;
	private String first;

	public Name(String line) throws UnknownObjectException, InvalidFormatException {		
		if (!line.startsWith("N:")) throw new InvalidFormatException("Name does not start with \"N:\"");
		if (line.contains(";")){
			String[] parts = line.split(";");
			if (parts.length>0) setLast(parts[0]);
			if (parts.length>1) setFirst(parts[1]);

			if (parts.length>2){
				System.err.println("Name with more than two parts found:");
				throw new NotImplementedException();
			}
		} else last=line.substring(2); 
		
	}
	
	private void setLast(String string) {
		if (string.isEmpty()) return;
		last=string;		
	}
	private void setFirst(String string) {
		if (string.isEmpty()) return;
		first=string;		
	}

	@Override
	public String toString() {
		return first+" "+last;
	}

}
