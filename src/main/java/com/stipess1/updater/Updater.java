package com.stipess1.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;


public class Updater
{
    private static final String USER_AGENT = "Updater by Stipess1";
    // Direct download link
    private String downloadLink;
    // Provided plugin
    private Plugin plugin;
    // The folder where update will be downloaded
    private File updateFolder;
    // The plugin file
    private File file;
    // ID of a project
    private String id;
    // return a page
    private int page = 1;
    // Set the update type
    private UpdateType updateType;
    // Get the outcome result
    private Result result = Result.DOWNLOADED;
    // If next page is empty set it to true, and get info from previous page.
    private boolean emptyPage;
    // Version returned from spigot
    private String version;

    private static final String DOWNLOAD = "/download";
    private static final String VERSIONS = "/versions";
    private static final String PAGE = "?page=";
    private static final String API_RESOURCE = "https://api.spiget.org/v2/resources/";

    public Updater(Plugin plugin, String id, File file, UpdateType updateType)
    {
        this.plugin = plugin;
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        this.id = id;
        this.file = file;
        this.updateType = updateType;

        downloadLink = API_RESOURCE + id + DOWNLOAD;
        checkUpdate();
    }

    public enum UpdateType
    {
        // Checks only the version
        VERSION_CHECK,
        // Downloads without checking the version
        DOWNLOAD,
        // If updater finds new version automatically, download it.
        CHECK_DOWNLOAD

    }

    public enum Result
    {

        UPDATE_FOUND,

        NO_UPDATE,

        DOWNLOADED,

        FAILED
    }

    /**
     * Get the result of the update.
     *
     * @return result of the update.
     * @see Result
     */
    public Result getResult()
    {
        return result;
    }

    /**
     * Get the latest version from spigot.
     *
     * @return latest version.
     */
    public String getVersion()
    {
        return version;
    }

    private void checkUpdate()
    {
        try
        {
            String page = Integer.toString(this.page);

            URL url = new URL(API_RESOURCE+id+VERSIONS+PAGE+page);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);
            JsonArray jsonArray = element.getAsJsonArray();

            if(jsonArray.size() == 10 && !emptyPage)
            {
                connection.disconnect();
                this.page++;
                checkUpdate();
            }
            else if(jsonArray.size() == 0)
            {
                emptyPage = true;
                this.page--;
                checkUpdate();
            }
            else if(jsonArray.size() < 10)
            {
                element = jsonArray.get(jsonArray.size()-1);

                JsonObject object = element.getAsJsonObject();
                element = object.get("name");
                version = element.toString().replaceAll("\"", "");

                plugin.getLogger().info("Checking for update...");
                if(shouldUpdate(version, plugin.getDescription().getVersion()) && updateType == UpdateType.VERSION_CHECK)
                {
                    result = Result.UPDATE_FOUND;
                    plugin.getLogger().info("Update found!");
                }
                else if(updateType == UpdateType.DOWNLOAD)
                {
                    plugin.getLogger().info("Downloading update... version not checked");
                    download();
                }
                else if(updateType == UpdateType.CHECK_DOWNLOAD)
                {
                    if(shouldUpdate(version, plugin.getDescription().getVersion()))
                    {
                        plugin.getLogger().info("Update found, downloading now...");
                        result = Result.UPDATE_FOUND;
                        download();
                    }
                    else
                    {
                        plugin.getLogger().info("Update not found");
                    }
                }
                else
                {
                    plugin.getLogger().info("Update not found");
                    result = Result.NO_UPDATE;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean shouldUpdate(String newVersion, String oldVersion)
    {
        return !newVersion.equalsIgnoreCase(oldVersion);
    }

    private void download()
    {
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try
        {
            URL url = new URL(downloadLink);
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(new File(updateFolder, file.getName()));

            final byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }
        }
        catch (Exception e)
        {
            plugin.getLogger().log(Level.SEVERE, "Updater tried to download the update, but was unsuccessful.");
            result = Result.FAILED;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

}
