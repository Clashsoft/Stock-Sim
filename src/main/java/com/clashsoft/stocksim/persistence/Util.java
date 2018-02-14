package com.clashsoft.stocksim.persistence;

import java.io.*;
import java.util.UUID;
import java.util.zip.ZipFile;

public class Util
{
	public static void writeUUID(DataOutput output, UUID uuid) throws IOException
	{
		output.writeLong(uuid.getMostSignificantBits());
		output.writeLong(uuid.getLeastSignificantBits());
	}

	public static UUID readUUID(DataInput input) throws IOException
	{
		long most = input.readLong();
		long least = input.readLong();
		return new UUID(most, least);
	}

	public static void dataInput(ZipFile zipFile, String file, DataInputReader consumer) throws IOException
	{
		try (DataInputStream dataInput = new DataInputStream(zipFile.getInputStream(zipFile.getEntry(file))))
		{
			consumer.read(dataInput);
		}
	}

	public static void dataInput(File file, DataInputReader consumer) throws IOException
	{
		try (DataInputStream dataInput = new DataInputStream(new BufferedInputStream(new FileInputStream(file))))
		{
			consumer.read(dataInput);
		}
	}

	public static void dataOutput(File file, DataOutputWriter consumer) throws IOException
	{
		try (DataOutputStream dataInput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))))
		{
			consumer.write(dataInput);
		}
	}

	public interface DataInputReader
	{
		void read(DataInput input) throws IOException;
	}

	public interface DataOutputWriter
	{
		void write(DataOutput output) throws IOException;
	}
}
