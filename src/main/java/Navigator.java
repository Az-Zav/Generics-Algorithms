import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp.Capability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.jline.keymap.KeyMap.ctrl;
import static org.jline.keymap.KeyMap.key;

public class Navigator {

    private enum Operation {
        UP,
        DOWN,
        ENTER
    }

    private final Terminal terminal;
    private final List<String> lines = new ArrayList<>();
    private Size size = new Size(0, 0);
    private final BindingReader bindingReader;

    public Navigator(Terminal terminal, String title, Collection<String> options) {
    this.terminal = terminal;
    this.bindingReader = new BindingReader(terminal.reader());
    lines.add(title);
    lines.addAll(options);
    }

    private List<AttributedString> displayLines(int cursorRow) {
    List<AttributedString> out = new ArrayList<>();
    int i = 0;
    for (String s : lines) {
        if (i == cursorRow) {
            out.add(new AttributedStringBuilder()
                    .append(s, AttributedStyle.INVERSE)
                    .toAttributedString());
        } else {
            out.add(new AttributedString(s));
        }
        i++;
    }
    return out;
    }

    private void bindKeys(KeyMap<Operation> map) {
    map.bind(Operation.DOWN, "e", ctrl('E'), key(terminal, Capability.key_down));
    map.bind(Operation.UP, "y", ctrl('Y'), key(terminal, Capability.key_up));
    map.bind(Operation.ENTER, "\r");
    }

    public String select() {
    Display display = new Display(terminal, true);
    Attributes attr = terminal.enterRawMode();
    try {
        terminal.puts(Capability.enter_ca_mode);
        terminal.puts(Capability.keypad_xmit);
        terminal.writer().flush();
        size = terminal.getSize();
        display.clear();
        display.reset();
        int selectRow = 1;
        KeyMap<Operation> keyMap = new KeyMap<>();
        bindKeys(keyMap);
        while (true) {
            display.resize(size.getRows(), size.getColumns());
            display.update(
                    displayLines(selectRow),
                    size.cursorPos(0, lines.get(0).length()));
            Operation op = bindingReader.readBinding(keyMap);
            switch (op) {
                case DOWN:
                    selectRow++;
                    if (selectRow > lines.size() - 1) {
                        selectRow = 1;
                    }
                    break;
                case UP:
                    selectRow--;
                    if (selectRow < 1) {
                        selectRow = lines.size() - 1;
                    }
                    break;
                case ENTER:
                    return lines.get(selectRow);
            }
        }
    } finally {
        terminal.setAttributes(attr);
        terminal.puts(Capability.exit_ca_mode);
        terminal.puts(Capability.keypad_local);
        terminal.writer().flush();
        }
    }

    public static int promptMenu(Terminal terminal, String title, List<String> options) throws IOException {
        Navigator navigator = new Navigator(terminal, title, options);
        String selected = navigator.select();
        return options.indexOf(selected) + 1;
    }

}