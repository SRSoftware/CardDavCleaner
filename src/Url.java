import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Url extends Mergable<Url> implements ChangeListener, Comparable<Url> {
	public static void test() {
		try {
			System.out.print("Url creation test (null)...");
			String testCase = null;
			try {
				Url nM = new Url(testCase);
				System.err.println("failed: " + nM);
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println("ok");
      }

			System.out.print("Url creation test (empty)...");
			testCase = "URL:";
			Url emptyN = new Url(testCase);
			if (emptyN.toString().equals(testCase) && !emptyN.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + emptyN);
				System.exit(-1);
			}

			System.out.print("Url creation test (simple)...");
			testCase = "URL:http://www.example.org";
			Url simplN = new Url(testCase);
			if (simplN.toString().equals(testCase) && !simplN.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + simplN);
				System.exit(-1);
			}

			System.out.print("Url creation test (work/invalid)...");
			testCase = "URL;TYPE=WORK:internet";
			Url workN = new Url(testCase);
			if (workN.toString().equals(testCase) && workN.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + workN);
				System.exit(-1);
			}

			System.out.print("Url creation test (home/valid)...");
			testCase = "URL;TYPE=HOME:http://example.com";
			Url homeN = new Url(testCase);
			if (homeN.toString().equals(testCase) && !homeN.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + homeN);
				System.exit(-1);
			}

			Url[] urlnames = { emptyN,simplN,workN,homeN };

			System.out.print("Url isEmpty test...");
			int comp = 0;
			int num = 0;
			for (Url m : urlnames) {
				comp++;
				if (!m.isEmpty()) {
					num++;
				} else if (m == emptyN) {
					num++;
				}
			}
			if (num == comp) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}

			System.out.print("Url compare test...");
			comp = 0;
			num = 0;
			for (Url m : urlnames) {
				comp++;
				if (m.compareTo(workN) != 0 && m.compareTo(workN) == -workN.compareTo(m)) {
					num++;
				} else {
					if (workN==m){
						num++;
					}
				}
			}
			if (comp == num) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}

			System.out.print("Url compatibility test...");
			comp = 0;
			num = 0;
			for (Url a : urlnames) {
				for (Url b : urlnames) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("URL", "").replace(";TYPE=WORK", "").replace(";TYPE=HOME", "").replaceFirst(":", "");
						if (concat.equals("http://www.example.org:internet") ||
								concat.equals("http://www.example.org:http://example.com") ||
								concat.equals("internet:http://www.example.org") ||
								concat.equals("internet:http://example.com") ||
								concat.equals("http://example.com:http://www.example.org") ||
								concat.equals("http://example.com:internet")) {
							comp++;
						} else {
							System.err.println(a + " <=> " + b);
						}
					}
				}
			}
			if (comp == num) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}
			
			System.out.print("Url clone test...");
			comp=0;
			num=0;
			for (Url m:urlnames){
				comp++;
				try {
					if (m.toString().equals(m.clone().toString())){
						num++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}

			System.out.print("Url merge test...");
			comp=0;
			num=0;
			for (Url m:urlnames){
				try {
					comp+=2;
					Url clone1=(Url) m.clone();
					Url clone2=(Url) workN.clone();
					
					if (clone1.mergeWith(workN) && clone1.toString().equals(workN.toString())) num++;
					if (clone2.mergeWith(m) && clone2.toString().equals(workN.toString())) num++;
					if (clone1.toString().equals("NICKNAME;TYPE=WORK;TYPE=INTERNET:Edward Snowden")) num++;
					if (clone2.toString().equals("NICKNAME;TYPE=WORK;TYPE=INTERNET:Edward Snowden")) num++;if (comp>num){
						if ((m.url!=null && !m.url.isEmpty()) && (workN.url!=null && !workN.url.isEmpty()) && !m.url.equals(workN.url)){
							num+=2;
						}
					}
					if (comp>num){
						System.out.println();
						System.out.println("fb: "+workN);
						System.out.println(" b: "+m);
						System.out.println("merged:");
						System.out.println("fb: "+clone2);
						System.out.println(" b: "+clone1);
					}
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
				System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
		} catch (UnknownObjectException e) {
      e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();

		}

	}	
	private boolean home=false;	
	private boolean work=false;
	private boolean invalid=false;
	private String url;
	private InputField urlField;
	private JCheckBox homeBox;
	private JCheckBox workBox;
	private VerticalPanel form;
	private Pattern ptr = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
	public Url(String content) throws UnknownObjectException, InvalidFormatException {
		if (content==null||!content.startsWith("URL")) throw new InvalidFormatException("Url does not start with \"URL\"");
		String line = content.substring(3);
		while(!line.startsWith(":")){
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			if (line.toUpperCase().startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			} 
			if (line.toUpperCase().startsWith("TYPE=WORK")){
				work=true;
				line=line.substring(9);
				continue;
			} 
			if (line.toUpperCase().startsWith("WORK=")){
				work=true;
				line=line.substring(5);
				continue;
			} 
			throw new UnknownObjectException(line+" in "+content);
		}
		readUrl(line.substring(1));		
	}

	public int compareTo(Url otherUrl) {
		return this.toString().compareTo(otherUrl.toString());
	}
	
	public VerticalPanel editForm() {
		form=new VerticalPanel("Web Adress");
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);
		form.add(urlField=new InputField("URL",url));
		urlField.addEditListener(this);
		form.add(homeBox=new JCheckBox("Home",home));
		homeBox.addChangeListener(this);
		form.add(workBox=new JCheckBox("Work",work));
		workBox.addChangeListener(this);
		form.scale();
		return form;
	}

	@Override
  public boolean isCompatibleWith(Url other) {
		if (different(url, other.url)) return false;
	  return true;
  }

	public boolean isEmpty() {
		return url==null || url.isEmpty();
	}

	@Override
  public boolean mergeWith(Url other) {
		if (!isCompatibleWith(other)) return false;
		url=merge(url, other.url);
		if (other.home) home=true;
		if (other.work) work=true;
	  return true;
  }

	public void stateChanged(ChangeEvent arg0) {
		update();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("URL");
		if (home) sb.append(";TYPE=HOME");
		if (work) sb.append(";TYPE=WORK");
		sb.append(":");
		if (url!=null)sb.append(url);
		return sb.toString();
	}
	
	private void checkValidity() {
		invalid=false;
		if (url==null) return;
		if (ptr.matcher(url).matches()){
			invalid=false;
		} else {
			invalid=true;
		}
	}

	private boolean isInvalid() {
	  return invalid;
  }

	private void readUrl(String line) {
		line=line.trim();
		if (line.isEmpty()) {
			line=null;
		}
		url = line;
		checkValidity();
	}

	private void update(){
		readUrl(urlField.getText());
		home=homeBox.isSelected();
		work=workBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.red:Color.green);
		}	
	}
	
	protected Object clone() throws CloneNotSupportedException {		
		try {
			return new Url(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
}
