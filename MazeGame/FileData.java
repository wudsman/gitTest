public class FileData
{
	public int hex;
	public float TwoDArray[][];
	public int locArray[];
	public int rotateArray[];
	public long seconds;

	FileData()
	{
		
		TwoDArray = new float[16][];
		locArray = new int[16];
		rotateArray = new int[16];
		seconds = Time.mSeconds;
	}
	public void printFD()
	{
		for(int i = 0; i< 16;i++)
		{
			System.out.println("Tile" + i);
			System.out.println("at location " + locArray[i]);
			System.out.println("with rotation " + rotateArray[i]);
			System.out.println("with lines ");
			for(int j = 0; j < TwoDArray[j].length;i++)
			{
				System.out.println(TwoDArray[i][j]);
			}
		}
	}
}