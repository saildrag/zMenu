package fr.maxlego08.menu.loader;

import java.io.File;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PermissibleButton;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.button.ZPermissibleButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.exceptions.InventoryButtonException;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.ItemStackLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class ZButtonLoader implements Loader<Button> {

	private final MenuPlugin plugin;
	private final File file;

	/**
	 * @param plugin
	 * @param file
	 */
	public ZButtonLoader(MenuPlugin plugin, File file) {
		super();
		this.plugin = plugin;
		this.file = file;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

		String buttonType = configuration.getString(path + "type");
		String buttonName = (String) objects[0];

		ButtonManager buttonManager = this.plugin.getButtonManager();
		Optional<ButtonLoader> optional = buttonManager.getLoader(buttonType);

		if (!optional.isPresent()) {
			throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path
					+ " in inventory " + this.file.getAbsolutePath());
		}

		Loader<ItemStack> itemStackLoader = new ItemStackLoader();

		ButtonLoader loader = optional.get();
		ZButton button = (ZButton) loader.load(configuration, path);

		button.setSlot(configuration.getInt(path + "slot", 0));
		button.setPermanent(configuration.getBoolean(path + "isPermanent", false));
		button.setItemStack(itemStackLoader.load(configuration, path + "item."));
		button.setButtonName(buttonName);

		if (button instanceof PermissibleButton) {

			ZPermissibleButton permissibleButton = (ZPermissibleButton) button;
			permissibleButton.setPermission(configuration.getString(path + "permission", null));

			if (configuration.contains(path + "else")) {

				Button elseButton = this.load(configuration, path + "else.", buttonName + ".else");
				permissibleButton.setElseButton(elseButton);

			}

		}

		if (button instanceof PlaceholderButton) {

			ZPlaceholderButton placeholderButton = (ZPlaceholderButton) button;
			placeholderButton.setPlaceholder(configuration.getString(path + "placeHolder", null));
			placeholderButton.setAction(PlaceholderAction.from(configuration.getString(path + "action", null)));
			placeholderButton.setValue(configuration.getString(path + "value", null));

		}

		return button;
	}

	@Override
	public void save(Button object, YamlConfiguration configuration, String path, Object... objects) {

	}

}
