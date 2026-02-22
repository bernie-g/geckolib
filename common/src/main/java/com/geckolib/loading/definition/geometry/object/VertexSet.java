package com.geckolib.loading.definition.geometry.object;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import com.geckolib.cache.model.GeoVertex;

/// Holder class to make it easier to store and refer to vertices for a given cube
public record VertexSet(GeoVertex bottomLeftBack, GeoVertex bottomRightBack, GeoVertex topLeftBack, GeoVertex topRightBack,
                        GeoVertex topLeftFront, GeoVertex topRightFront, GeoVertex bottomLeftFront, GeoVertex bottomRightFront) {
        public VertexSet(Vec3 origin, Vec3 vertexSize, double inflation) {
            this(
                    new GeoVertex(origin.x - inflation, origin.y - inflation, origin.z - inflation),
                    new GeoVertex(origin.x - inflation, origin.y - inflation, origin.z + vertexSize.z + inflation),
                    new GeoVertex(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
                    new GeoVertex(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z + vertexSize.z + inflation),
                    new GeoVertex(origin.x + vertexSize.x + inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
                    new GeoVertex(origin.x + vertexSize.x + inflation, origin.y + vertexSize.y + inflation, origin.z + vertexSize.z + inflation),
                    new GeoVertex(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z - inflation),
                    new GeoVertex(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z + vertexSize.z + inflation));
        }

        /// Returns the normal vertex array for a west-facing quad
        public GeoVertex[] quadWest() {
            return new GeoVertex[] {this.topRightBack, this.topLeftBack, this.bottomLeftBack, this.bottomRightBack};
        }

        /// Returns the normal vertex array for an east-facing quad
        public GeoVertex[] quadEast() {
            return new GeoVertex[] {this.topLeftFront, this.topRightFront, this.bottomRightFront, this.bottomLeftFront};
        }

        /// Returns the normal vertex array for a north-facing quad
        public GeoVertex[] quadNorth() {
            return new GeoVertex[] {this.topLeftBack, this.topLeftFront, this.bottomLeftFront, this.bottomLeftBack};
        }

        /// Returns the normal vertex array for a south-facing quad
        public GeoVertex[] quadSouth() {
            return new GeoVertex[] {this.topRightFront, this.topRightBack, this.bottomRightBack, this.bottomRightFront};
        }

        /// Returns the normal vertex array for a top-facing quad
        public GeoVertex[] quadUp() {
            return new GeoVertex[] {this.topRightBack, this.topRightFront, this.topLeftFront, this.topLeftBack};
        }

        /// Returns the normal vertex array for a bottom-facing quad
        public GeoVertex[] quadDown() {
            return new GeoVertex[] {this.bottomLeftBack, this.bottomLeftFront, this.bottomRightFront, this.bottomRightBack};
        }

        /// Return the vertex array relevant to the quad being built, taking into account mirroring and quad type
        public GeoVertex[] verticesForQuad(Direction direction, boolean boxUv, boolean mirror) {
            return switch (direction) {
                case WEST -> mirror ? quadEast() : quadWest();
                case EAST -> mirror ? quadWest() : quadEast();
                case NORTH -> quadNorth();
                case SOUTH -> quadSouth();
                case UP -> mirror && !boxUv ? quadDown() : quadUp();
                case DOWN -> mirror && !boxUv ? quadUp() : quadDown();
            };
        }
    }