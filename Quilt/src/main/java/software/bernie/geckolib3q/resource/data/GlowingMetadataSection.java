package software.bernie.geckolib3q.resource.data;

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

	public record Section(int x1, int y1, int x2, int y2) {
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

	}

}
