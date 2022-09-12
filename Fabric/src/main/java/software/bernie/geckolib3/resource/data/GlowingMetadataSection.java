package software.bernie.geckolib3.resource.data;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * Metadata for auto glowing textures
 * 
 * Originally developed for chocolate quest repoured
 */
public class GlowingMetadataSection {

	@FunctionalInterface
	public interface BiIntConsumer {

		void accept(int x, int y);

	}

	public static final GlowingMetadataSectionSerializer SERIALIZER = new GlowingMetadataSectionSerializer();

	private final Collection<Section> glowingSections;

	public GlowingMetadataSection(Stream<Section> sections) {
		this.glowingSections = sections.map(Section::copy).collect(Collectors.toList());
	}

	public Collection<Section> getGlowingSections() {
		return Collections.unmodifiableCollection(this.glowingSections);
	}

	public boolean isEmpty() {
		return this.glowingSections.isEmpty();
	}

	public static class Section {

		private final int x1;
		private final int y1;
		private final int x2;
		private final int y2;

		public Section(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public Section copy() {
			return new Section(x1, y1, x2, y2);
		}

		public void forEach(BiIntConsumer action) {
			for (int x = x1; x < x2; x++) {
				for (int y = y1; y < y2; y++) {
					action.accept(x, y);
				}
			}
		}

		public int getX1() {
			return x1;
		}

		public int getY1() {
			return y1;
		}

		public int getX2() {
			return x2;
		}

		public int getY2() {
			return y2;
		}

	}

}
