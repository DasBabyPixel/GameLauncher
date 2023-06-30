/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.service;

import gamelauncher.engine.data.embed.EmbedFileSystem;
import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.render.font.FontFactory;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.util.i18n.LanguageManager;
import gamelauncher.engine.util.keybind.KeybindManager;

import java.nio.file.FileSystem;

public class ServiceReference<T> {

    public static final ServiceReference<EventManager> EVENT_MANAGER = new ServiceReference<>(EventManager.class);
    public static final ServiceReference<LanguageManager> LANGUAGE_MANAGER = new ServiceReference<>(LanguageManager.class);
    public static final ServiceReference<TextureManager> TEXTURE_MANAGER = new ServiceReference<>(TextureManager.class);
    public static final ServiceReference<ModelLoader> MODEL_LOADER = new ServiceReference<>(ModelLoader.class);
    public static final ServiceReference<ResourceLoader> RESOURCE_LOADER = new ServiceReference<>(ResourceLoader.class);
    public static final ServiceReference<KeybindManager> KEYBIND_MANAGER = new ServiceReference<>(KeybindManager.class);
    public static final ServiceReference<NetworkClient> NETWORK_CLIENT = new ServiceReference<>(NetworkClient.class);
    public static final ServiceReference<GuiManager> GUI_MANAGER = new ServiceReference<>(GuiManager.class);
    public static final ServiceReference<GlyphProvider> GLYPH_PROVIDER = new ServiceReference<>(GlyphProvider.class);
    public static final ServiceReference<ShaderLoader> SHADER_LOADER = new ServiceReference<>(ShaderLoader.class);
    public static final ServiceReference<FontFactory> FONT_FACTORY = new ServiceReference<>(FontFactory.class);
    public static final ServiceReference<FileSystem> EMBED_FILE_SYSTEM = new ServiceReference<>(EmbedFileSystem.serviceName, FileSystem.class);

    private final ServiceName serviceName;
    private final Class<T> serviceClass;

    public ServiceReference(Class<T> serviceClass) {
        this(ServiceProvider.DEFAULT, serviceClass);
    }

    public ServiceReference(ServiceName serviceName, Class<T> serviceClass) {
        this.serviceName = serviceName;
        this.serviceClass = serviceClass;
    }

    public ServiceName serviceName() {
        return serviceName;
    }

    public Class<T> serviceClass() {
        return serviceClass;
    }
}
