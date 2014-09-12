/**
 *  Author :  hmg25
 *  Description :
 */
package com.miles.ebook.bookwidget;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;

public class BookPageFactory {

	private File bookFile = null;
	private MappedByteBuffer mFileMapper = null;
	private int mBookLength = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	private final String CHAR_FORMAT = "GBK";
	private Bitmap mBookBg = null;
	private int mWidth;
	private int mHeight;

	private Vector<String> mNowPageBookVector = new Vector<String>();

	private int m_fontSize = 24;
	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffff9e85; // ������ɫ
	private int marginWidth = 15; // �������Ե�ľ���
	private int marginHeight = 20; // �������Ե�ľ���

	private int mLineCount; // ÿҳ������ʾ������
	private float mVisibleHeight; // �������ݵĿ�
	private float mVisibleWidth; // �������ݵĿ�
	private boolean mIsfirstPage, mIsNowLastPage;

	// private int m_nLineSpaceing = 5;

	private Paint mPaint;

	public BookPageFactory(int w, int h) {
		// TODO Auto-generated constructor stub
		mWidth = w;
		mHeight = h;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(m_fontSize);
		mPaint.setColor(m_textColor);
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight * 2;
		mLineCount = (int) (mVisibleHeight / m_fontSize); // ����ʾ������
	}

	public void openBook(String strFilePath) throws IOException {
		bookFile = new File(strFilePath);
		long lLen = bookFile.length();//�����ļ���С
		mBookLength = (int) lLen;
		mFileMapper = new RandomAccessFile(bookFile, "r").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);
	}

	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (CHAR_FORMAT.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = mFileMapper.get(i);
				b1 = mFileMapper.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (CHAR_FORMAT.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = mFileMapper.get(i);
				b1 = mFileMapper.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = mFileMapper.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = mFileMapper.get(i + j);
		}
		return buf;
	}

	// ��ȡ��һ����
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// ��ݱ����ʽ�жϻ���
		if (CHAR_FORMAT.equals("UTF-16LE")) {
			while (i < mBookLength - 1) {
				b0 = mFileMapper.get(i++);
				b1 = mFileMapper.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (CHAR_FORMAT.equals("UTF-16BE")) {
			while (i < mBookLength - 1) {
				b0 = mFileMapper.get(i++);
				b1 = mFileMapper.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < mBookLength) {
				b0 = mFileMapper.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = mFileMapper.get(nFromPos + i);
		}
		return buf;
	}

	protected Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < mBookLength) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // ��ȡһ������
			m_mbBufEnd += paraBuf.length;
			try {
				strParagraph = new String(paraBuf, CHAR_FORMAT);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(CHAR_FORMAT).length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, CHAR_FORMAT);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(CHAR_FORMAT).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	protected void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			mIsfirstPage = true;
			return;
		} else
			mIsfirstPage = false;
		mNowPageBookVector.clear();
		pageUp();
		mNowPageBookVector = pageDown();
	}

	public void nextPage() throws IOException {
		if (m_mbBufEnd >= mBookLength) {
			mIsNowLastPage = true;
			return;
		} else
			mIsNowLastPage = false;
		mNowPageBookVector.clear();
		m_mbBufBegin = m_mbBufEnd;
		mNowPageBookVector = pageDown();
	}

	/**����һҳͼ��*/
	public void onDraw(Canvas c) {
		if (mNowPageBookVector.size() == 0)
			mNowPageBookVector = pageDown();
		if (mNowPageBookVector.size() > 0) {
			if (mBookBg == null)
				c.drawColor(m_backColor);
			else
				// c.drawBitmap(m_book_bg, 0, 0, null);
				c.drawBitmap(mBookBg, null, new RectF(0, 0, mWidth,
						mHeight), null);
			int y = marginHeight;
			for (String strLine : mNowPageBookVector) {
				y += m_fontSize;
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}
		float fPercent = (float) (m_mbBufBegin * 1.0 / mBookLength);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
	}

	public void setBgBitmap(Bitmap BG) {
		mBookBg = BG;
	}

	public boolean isfirstPage() {
		return mIsfirstPage;
	}

	public boolean islastPage() {
		return mIsNowLastPage;
	}
}
