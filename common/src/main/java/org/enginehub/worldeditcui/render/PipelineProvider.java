package org.enginehub.worldeditcui.render;

/**
 * A wrapper around the WECUI pipeline, to allow manipulating the state used by various shader mods.
 */
public interface PipelineProvider {
    String id();
    boolean available();

    default boolean shouldRender() {
        return true;
    }

    /**
     * Create a render sink to be used for further operations.
     *
     * <p>This method will be called once to initialize the pipeline, and only if {@link #available()} is {@code true}</p>
     *
     * @return a sink for rendering operations
     */
    RenderSink provide();

}
