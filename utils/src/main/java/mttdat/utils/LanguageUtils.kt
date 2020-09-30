package mttdat.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import java.util.*

object LanguageUtils {
    private var languages: ArrayList<Language> = ArrayList()
    var currentLanguage: Language? = null

    fun changeLanguage(context: Context, language: Language): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfig(context, language)
        } else updateConfig(context, language)
    }

    /**
     * Notice: (2 options)
     *
     * 1. Use updateConfiguration() for all API levels in onCreate of your Application and BaseActivity
     * to update the resources ignoring the deprecation. Remember to deal with the cache issue in this case.
     *
     * 2. Use updateConfiguration() for API < 17 and createConfigurationContext for API ≥ 17, respect to the deprecation.
     * Additionally, you have to set Activity titles manually using local resources
     * (Check Appendix A If you do use title of activity; otherwise, skip it)
     *
     * In lower API, it doesn't separate app context and activity context
     * --> So, either update or create new config, they're the same.
     * However in higher API, you have to handle context manually in individual activity and app, too.
     * --> attachBaseContext() is recommended where to update configuration:
     *
     * @Override
     * protected void attachBaseContext(Context base) {
     *      // Attach a new updated configuration context to this activity and all fragment belonging to it .
     *      super.attachBaseContext(LanguageUtils.getInstance().changeLanguage(base,
     *          LanguageUtils.getInstance().getCurrentLanguage()));
     * }
     *
     * Appendix A:
     *
     * public abstract class BaseActivity extends AppCompatActivity {
     *
     *      @Override
     *      protected void onCreate(Bundle savedInstanceState) {
     *          super.onCreate(savedInstanceState);
     *          resetTitle();
     *     }
     *
     *     private void resetTitle() {
     *     try {
     *          int label = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA).labelRes;
     *          if (label != 0) { setTitle(label);}
     *     } catch (NameNotFoundException e) { ... }
     * }
     *
     * ...
     * */
    private fun updateConfig(context: Context, language: Language): Context {

        // Change locale of context's system.
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        // This is deprecated. Update an old context and reuse it.
        resources.updateConfiguration(configuration, resources.displayMetrics)

        addLanguage(language)

        return context
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun createConfig(context: Context, language: Language): Context {

        // Change locale of app's system.
        val locale = Locale(language.code)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        addLanguage(language)

        // After getting to updating new configuration from the old context, create new context base on
        // an updated configuration.
        return context.createConfigurationContext(configuration)
    }

    private fun addLanguage(language: Language){
        currentLanguage = language

        if(!languages.contains(language)){
            languages.add(language)
        }
    }

    class Language {
        var name: String = ""
        var code: String = ""

        constructor(name: String, code: String) {
            this.name = name
            this.code = code
        }

        constructor(code: String) {
            this.code = code
        }
    }
}