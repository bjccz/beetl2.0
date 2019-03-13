package org.beetl.core.text;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 有问题 ：<%a%>a\n<%c%>  a\n 应该保留，但去掉了
 * @author xiandafu
 *
 */
public class FragmentFormmater {
	List<Fragment> list;

	public FragmentFormmater(List<Fragment> list) {
		this.list = list;
	}

	public void format() {
		ListIterator<Fragment> it = list.listIterator();
		int start = 0;
		LineStatus lineStatus = new LineStatus();
		while (it.hasNext()) {
			Fragment f = it.next();
			if (f.startLine == start) {
				//同行格式化
				lineStatus.add(f);
			} else {
				lineStatus.format();
				Fragment nextFragment = lineStatus.lastFragment();
				lineStatus.reset();
				//如果最后一个一行跨行，加入到下一次格式化
				if (nextFragment != null && nextFragment.startLine != nextFragment.endLine
						) {
					// 放到下一次比较
					lineStatus.add(nextFragment);
				}
				lineStatus.add(f);
				start = f.startLine;
			}
		}
		lineStatus.format();
		lineStatus.reset();

	}

	/**
	 * 同一行内容
	 * 
	 * @author xiandafu
	 *
	 */
	class LineStatus {

		List<Fragment> list = new ArrayList<Fragment>(8);
		boolean hasScript = false;
		boolean hasHolder = false;
		boolean hasText = false;

		public void add(Fragment fr) {
			list.add(fr);
			if (fr instanceof ScriptFragment) {
				hasScript = true;
			} else if (fr instanceof PlaceHolderFragment) {
				hasHolder = true;
			} else if (fr instanceof TextFragment) {
				hasText = true;
			} else if (fr instanceof HtmlTagStartFragment || fr instanceof HtmlTagEndFragment) {
				hasScript = true;
				;
			} else {
				throw new RuntimeException("不支持的fr");
			}
		}

		public Fragment lastFragment() {
			Fragment f = null;
			if (list.size() == 1) {
				f = list.get(0);
			} else {
				f = list.get(list.size() - 1);
			}

			return f;

		}

		public void format() {
			
			if(!(hasScript&&hasText&&!hasHolder)) {
				//只有文本和脚本情况下需要格式化
				return ;
			}

			// 格式化
			int size = list.size();
			for (int i = 0; i < size; i++) {
				Fragment f = list.get(i);
				if (f instanceof TextFragment) {
					TextFragment text = (TextFragment) f;
					if (i == 0) {
						text.formatEndPart();
					} else if (i == size - 1) {
						text.formatStartPart();
					} else {
						text.foramtSpace();

					}
				}


			}

			reset();

		}

		private void reset() {
			list.clear();
			hasScript = false;
			hasHolder = false;
			hasText = false;
		}
	}
}
