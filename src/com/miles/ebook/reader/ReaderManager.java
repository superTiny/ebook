package com.miles.ebook.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;

public class ReaderManager
{
	private static final String TAG = ReaderManager.class.getSimpleName();

	/**
	 * 换行符
	 * */
	private static final byte B_CHANGE_ROW = 0x0a;

	/**
	 * 回车键
	 * */
	private static final byte B_ENTER = 0x0d;

	private static ReaderManager mManager;

	private long mBookLength = 0;

	private MappedByteBuffer mFileMapper;

	public static ReaderManager getInstance(Context mContext)
	{

		if (mManager == null)
		{
			synchronized (ReaderManager.class)
			{
				mManager = new ReaderManager(mContext.getApplicationContext());
			}
		}
		return mManager;
	}

	private ReaderManager(Context mContext)
	{
		
	}

	public void openBook(String strFilePath) throws IOException
	{
		String textPath = Environment.getExternalStorageDirectory()
				+ strFilePath;
		File bookFile = new File(textPath);
		long lLen = bookFile.length();// 返回文件大小
		mBookLength = (int) lLen;
		mFileMapper = new RandomAccessFile(bookFile, "r").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);
	}

	/** 读取下一段落 */
	protected byte[] readParagraphForward(int nFromPos)
	{
		int nStart = nFromPos;
		int i = nStart;
		byte b0;
		while (i < mBookLength)
		{
			b0 = mFileMapper.get(i++);
			if (b0 == B_CHANGE_ROW)
			{
				break;
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++)
		{
			buf[i] = mFileMapper.get(nFromPos + i);
		}
		return buf;
	}

	/** 读取上一段落 */
	protected byte[] readParagraphBack(int nFromPos)
	{
		int nEnd = nFromPos;
		int i;
		byte b0;
		i = nEnd - 1;
		while (i > 0)
		{
			b0 = mFileMapper.get(i);
			if (b0 == B_CHANGE_ROW && i != nEnd - 1)
			{
				i++;
				break;
			}
			i--;
		}
		if (i < 0) i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++)
		{
			buf[j] = mFileMapper.get(i + j);
		}
		return buf;
	}
}
