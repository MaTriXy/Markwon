package ru.noties.markwon.image.file;

import android.content.res.AssetManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ru.noties.markwon.image.ImageItem;
import ru.noties.markwon.image.SchemeHandler;

/**
 * @since 3.0.0
 */
public class FileSchemeHandler extends SchemeHandler {

    public static final String SCHEME = "file";

    /**
     * @see ru.noties.markwon.urlprocessor.UrlProcessorAndroidAssets
     */
    @NonNull
    public static FileSchemeHandler createWithAssets(@NonNull AssetManager assetManager) {
        return new FileSchemeHandler(assetManager);
    }

    @NonNull
    public static FileSchemeHandler create() {
        return new FileSchemeHandler(null);
    }

    private static final String FILE_ANDROID_ASSETS = "android_asset";

    @Nullable
    private final AssetManager assetManager;

    @SuppressWarnings("WeakerAccess")
    FileSchemeHandler(@Nullable AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @NonNull
    @Override
    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

        final List<String> segments = uri.getPathSegments();
        if (segments == null
                || segments.size() == 0) {
            // pointing to file & having no path segments is no use
            throw new IllegalStateException("Invalid file path: " + raw);
        }

        final InputStream inputStream;

        final boolean assets = FILE_ANDROID_ASSETS.equals(segments.get(0));
        final String fileName = uri.getLastPathSegment();

        if (assets) {

            // no handling of assets here if we have no assetsManager
            if (assetManager != null) {

                final StringBuilder path = new StringBuilder();
                for (int i = 1, size = segments.size(); i < size; i++) {
                    if (i != 1) {
                        path.append('/');
                    }
                    path.append(segments.get(i));
                }
                // load assets

                try {
                    inputStream = assetManager.open(path.toString());
                } catch (IOException e) {
                    throw new IllegalStateException("Exception obtaining asset file: " +
                            "" + raw + ", path: " + path.toString(), e);
                }
            } else {
                throw new IllegalStateException("Supplied file path points to assets, " +
                        "but FileSchemeHandler was not supplied with AssetsManager. " +
                        "Use `#createWithAssets` factory method to create FileSchemeHandler " +
                        "that can handle android assets");
            }

        } else {

            final String path = uri.getPath();
            if (TextUtils.isEmpty(path)) {
                throw new IllegalStateException("Invalid file path: " + raw + ", " + path);
            }

            try {
                inputStream = new BufferedInputStream(new FileInputStream(new File(path)));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Exception reading file: " + raw, e);
            }
        }

        final String contentType = MimeTypeMap
                .getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileName));

        return ImageItem.withDecodingNeeded(contentType, inputStream);
    }
}
