import java.util.Comparator;

public class TermSorter implements Comparator<Termostat>
{
	@Override
	public int compare(Termostat t1, Termostat t2)
	{
		// compare two elements
		return ((t1.temp).compareTo(t2.temp));
	}

}
