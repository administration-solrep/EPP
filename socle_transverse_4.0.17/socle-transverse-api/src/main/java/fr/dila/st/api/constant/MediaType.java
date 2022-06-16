package fr.dila.st.api.constant;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public enum MediaType {
    // Applications
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_JAVASCRIPT("application/javascript", "js"),
    APPLICATION_XML("application/xml", "xml"),
    APPLICATION_JSON("application/json", "json"),
    APPLICATION_ZIP("application/zip", "zip"),
    APPLICATION_JAR("application/jar", "jar"),
    APPLICATION_RAR("application/rar", "rar"),
    APPLICATION_7Z("application/x-7z-compressed", "7z"),
    APPLICATION_TAR_GZ("application/x-tgz", "tar.gz"),
    APPLICATION_KML("application/vnd.google-earth.kml+xml", "kml"),
    APPLICATION_KMZ("application/vnd.google-earth.kmz", "kmz"),
    APPLICATION_GPX("application/gpx+xml", "gpx"),
    APPLICATION_PDF("application/pdf", "pdf"),
    APPLICATION_SWF("application/x-shockwave-flash", "swf"),

    // Applications > MS Office
    APPLICATION_MS_WORD("application/msword", "doc"),
    APPLICATION_MS_EXCEL("application/vnd.ms-excel", "xls"),
    APPLICATION_MS_POWERPOINT("application/vnd.ms-powerpoint", "ppt"),

    APPLICATION_OPENXML_WORD("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    APPLICATION_OPENXML_EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    APPLICATION_OPENXML_POWERPOINT("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),

    // Applications > Open Office
    APPLICATION_ODF_TEXT("application/vnd.oasis.opendocument.text", "odt"),
    APPLICATION_ODF_SPREADSHEET("application/vnd.oasis.opendocument.spreadsheet", "ods"),
    APPLICATION_ODF_PRESENTATION("application/vnd.oasis.opendocument.presentation", "odp"),
    APPLICATION_ODF_CHART("application/vnd.oasis.opendocument.chart", "odc"),
    APPLICATION_ODF_GRAPHIC("application/vnd.oasis.opendocument.graphics", "odg"),
    APPLICATION_ODF_IMAGE("application/vnd.oasis.opendocument.image", "odi"),

    APPLICATION_RTF("application/rtf", "rtf"),

    // Text
    TEXT_PLAIN("text/plain", "plain"),
    TEXT_CSS("text/css", "css"),
    TEXT_CSV("text/csv", "csv"),
    TEXT_HTML("text/html", "html", Lists.newArrayList("htm")),
    TEXT_CALENDAR("text/calendar", "ics"),
    TEXT_JAVASCRIPT("text/javascript", "js"),
    TEXT_JSON("text/json", "json"),
    TEXT_RTF("text/rtf", "rtf"),

    // Images
    IMAGE_ANY("image/*"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_JPEG("image/jpeg", Lists.newArrayList("image/pjpeg"), "jpg", Lists.newArrayList("jpe", "jpeg")),
    IMAGE_PNG("image/png", Lists.newArrayList("image/x-png"), "png"),
    IMAGE_TIFF("image/tiff", "tif", Lists.newArrayList("tiff")),
    IMAGE_X_ICON("image/x-icon", "ico"),
    IMAGE_SVG("image/svg+xml", "svg"),
    IMAGE_BMP("image/x-ms-bmp", "bmp"),
    IMAGE_JPEG2000("image/jp2", "jp2"),
    IMAGE_PSD("image/x-photoshop", "psd"),

    // Visio
    APPLICATION_VISIO("application/vnd.visio", "vsd", Lists.newArrayList("vst", "vsw", "vss")),

    // Video
    VIDEO_ANY("video/*"),

    // Audio
    AUDIO_ANY("audio/*");

    private final String primaryMimeType;

    private final List<String> additionalMimeTypes;

    private final String primaryExtension;

    private final List<String> additionalExtensions;

    private static final Map<String, MediaType> MIME_TYPE_MAPPING = new HashMap<>();

    private static final Map<String, MediaType> EXTENSION_MAPPING = new HashMap<>();

    static {
        Stream
            .of(values())
            .forEach(
                mimeType -> {
                    mimeType
                        .supportedMimeTypes()
                        .forEach(mimeTypeString -> MIME_TYPE_MAPPING.put(mimeTypeString, mimeType));
                    mimeType.supportedExtensions().forEach(extension -> EXTENSION_MAPPING.put(extension, mimeType));
                }
            );
    }

    MediaType(String mimeType) {
        this(mimeType, Lists.newArrayListWithExpectedSize(2), null, Lists.newArrayListWithExpectedSize(2));
    }

    MediaType(String mimeType, String primaryExtension) {
        this(mimeType, Lists.newArrayListWithExpectedSize(2), primaryExtension, Lists.newArrayListWithExpectedSize(2));
    }

    MediaType(String mimeType, List<String> additionalMimeTypes, String primaryExtension) {
        this(mimeType, additionalMimeTypes, primaryExtension, Lists.newArrayListWithExpectedSize(2));
    }

    MediaType(String mimeType, String primaryExtension, List<String> additionalExtensions) {
        this(mimeType, Lists.newArrayListWithExpectedSize(2), primaryExtension, additionalExtensions);
    }

    MediaType(
        String mimeType,
        List<String> additionalMimeTypes,
        String primaryExtension,
        List<String> additionalExtensions
    ) {
        this.primaryMimeType = mimeType;
        this.additionalMimeTypes = additionalMimeTypes;
        this.primaryExtension = primaryExtension;
        this.additionalExtensions = additionalExtensions;
    }

    private static String normalize(String string) {
        if (string == null) {
            return null;
        }
        return string.toLowerCase(Locale.ROOT);
    }

    public String mime() {
        return primaryMimeType;
    }

    public String mimeUtf8() {
        StringBuilder sb = new StringBuilder(primaryMimeType);
        sb.append(";charset=UTF-8");
        return sb.toString();
    }

    public String extension() {
        return primaryExtension;
    }

    public List<String> supportedExtensions() {
        List<String> supportedExtensions = Lists.newArrayList(additionalExtensions);

        if (primaryExtension != null) {
            supportedExtensions.add(0, primaryExtension);
        }

        return supportedExtensions;
    }

    public List<String> supportedMimeTypes() {
        List<String> supportedMimeTypes = Lists.newArrayList(additionalMimeTypes);

        if (primaryMimeType != null) {
            supportedMimeTypes.add(0, primaryMimeType);
        }

        return supportedMimeTypes;
    }

    public boolean supports(String extension) {
        if (extension == null) {
            return false;
        }
        return supportedExtensions().contains(normalize(extension));
    }

    public static MediaType fromMimeType(String mimeType) {
        if (mimeType == null) {
            return null;
        }

        String mimeTypeWithoutParameters;
        int parameterStart = mimeType.indexOf(';');
        if (parameterStart == -1) {
            mimeTypeWithoutParameters = normalize(mimeType);
        } else {
            mimeTypeWithoutParameters = normalize(mimeType.substring(0, parameterStart));
        }
        return MIME_TYPE_MAPPING.get(mimeTypeWithoutParameters);
    }

    public static MediaType fromExtension(String extension) {
        if (extension == null) {
            return null;
        }
        return EXTENSION_MAPPING.get(normalize(extension));
    }

    public boolean isEqualToMimeType(String mimeType) {
        return this == fromMimeType(mimeType);
    }
}
