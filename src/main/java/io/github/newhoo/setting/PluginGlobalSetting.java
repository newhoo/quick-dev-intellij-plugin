package io.github.newhoo.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PluginGlobalSetting
 *
 * @link http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html
 * @link https://github.com/dubreuia/intellij-plugin-save-actions/blob/master/src/com/dubreuia/model/Storage.java#L11
 */
@State(name = "QuickDevPluginSettings", storages = {@com.intellij.openapi.components.Storage(value = "QuickDev_v1_PluginSettings.xml")})
public class PluginGlobalSetting implements PersistentStateComponent<PluginGlobalSetting> {

    private String apolloBaseUrl = "";

    public static PluginGlobalSetting getInstance() {
        return ServiceManager.getService(PluginGlobalSetting.class);
    }

    @Nullable
    @Override
    public PluginGlobalSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginGlobalSetting state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getApolloBaseUrl() {
        return apolloBaseUrl;
    }

    public void setApolloBaseUrl(String apolloBaseUrl) {
        this.apolloBaseUrl = apolloBaseUrl;
    }
}