package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Message;

/**
 * Filters
 *
 * @author sunbowen
 * @date 2016/5/18-15:26
 * @desc Filter的集合
 */
public class Filters {

	private final Filter isConsumed;

	private static final Filters INSTANCE = new Filters();

	private Filters() {
		isConsumed = new IsConsumedFilter();
	}

	public static Filter klass(Class<?> cls) {
		return new ClassFilter(cls);
	}

	public static Filter isConsumed() {
		return INSTANCE.isConsumed;
	}

	public static Filter messageId(int id) {
		return new MessageIdFilter(id);
	}

	static class ClassFilter implements Filter {

		private final Class<?> cls;

		public ClassFilter(Class<?> cls) {
			this.cls = cls;
		}

		@Override
		public boolean filter(Message message) {
			boolean result = false;
			if (message != null) {
				if (message.checkAndGet(this.cls) != null) {
					result = true;
				}
			}
			return result;
		}
	}

	static class IsConsumedFilter implements Filter {

		@Override
		public boolean filter(Message message) {
			boolean result = false;
			if (message != null) {
				result = !message.isConsumed;
			}
			return result;
		}
	}

	static class MessageIdFilter implements Filter {

		private final int mId;

		public MessageIdFilter(int id) {
			this.mId = id;
		}

		@Override
		public boolean filter(Message message) {
			boolean result = false;
			if (message != null) {
				if (this.mId == message.messageId) {
					result = true;
				}
			}
			return result;
		}
	}
}
