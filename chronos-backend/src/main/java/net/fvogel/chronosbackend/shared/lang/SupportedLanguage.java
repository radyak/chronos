package net.fvogel.chronosbackend.shared.lang;

/**
 * Languages that are supported globally for following contexts:
 *  - i18n of the UI
 *  - Wikipedia articles
 */
public enum SupportedLanguage {
    DE("de"),
    EN("en"),
    IT("it");

    private String lang;

    SupportedLanguage(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return this.lang;
    }
}
