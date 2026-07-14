package top.mcmtr.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

public final class ScreenWidgetHelper {

	private static final Method ADD_RENDERABLE_WIDGET = resolveAddRenderableWidget();

	private ScreenWidgetHelper() {
	}

	public static Button addButton(Screen screen, Button button) {
		try {
			return (Button) ADD_RENDERABLE_WIDGET.invoke(screen, button);
		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new RuntimeException("Failed to add renderable widget", exception);
		}
	}

	private static Method resolveAddRenderableWidget() {
		try {
			Method method = Screen.class.getDeclaredMethod("addRenderableWidget", GuiEventListener.class);
			method.setAccessible(true);
			return method;
		} catch (NoSuchMethodException exception) {
			throw new RuntimeException("Failed to resolve Screen.addRenderableWidget", exception);
		}
	}
}
