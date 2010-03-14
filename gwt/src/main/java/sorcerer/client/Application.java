package sorcerer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import sorcerer.client.data.SourceFileLoader;
import sorcerer.client.data.pkg.ClassListLoader;
import sorcerer.client.data.pkg.ProjectLoader;
import sorcerer.client.pkg.PackageTreeWidget;
import sorcerer.client.sourceview.SourceViewWidget;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {
    private SourceViewWidget mainCanvas;
    private static Application INSTANCE;
    
    public void onModuleLoad() {
        INSTANCE = this;
        Page.setAppImgDir("resource-files/");

        HLayout h = new HLayout();
        h.setWidth100();
        h.setHeight100();

        h.addMember(createLeft());
        h.addMember(createRightPane());

        RootPanel.get().add(h);

        SourceFileLoader.export();
        ProjectLoader.export();
        ClassListLoader.export();

        ProjectLoader.INSTANCE.load("data");
    }

    private SectionStack createLeft() {
        SectionStackSection pkg = new SectionStackSection("Package");
        pkg.setExpanded(true);
        pkg.setCanCollapse(true);
        pkg.addItem(new PackageTreeWidget());

        SectionStackSection outline = new SectionStackSection("Outline");
        outline.setExpanded(true);
        outline.setCanCollapse(true);
        outline.addItem(new HelpCanvas("help2"));

        SectionStack left = new SectionStack();
        left.setVisibilityMode(VisibilityMode.MULTIPLE);
        left.setHeight100();
        left.setWidth(200);
        left.addSection(pkg);
        left.addSection(outline);
        left.setShowResizeBar(true);
        return left;
    }

    private SectionStack createRightPane() {
        SectionStackSection pkg = new SectionStackSection("Source code");
        pkg.setExpanded(true);
        pkg.setShowHeader(false);
        pkg.addItem(mainCanvas=new SourceViewWidget());

        SectionStackSection search = new SectionStackSection("Search Result");
        search.setExpanded(false);
        search.setCanCollapse(true);
        search.addItem(new HelpCanvas("help4"));

        SectionStack right = new SectionStack();
        right.setVisibilityMode(VisibilityMode.MULTIPLE);
        right.setHeight100();
        right.setWidth100();
        right.addSection(pkg);
        right.addSection(search);

        return right;
    }

    class HelpCanvas extends Canvas {
        private String contents =  "<b>Severity 1</b> - Critical problem<br>System is unavailable in production or " +
              "is corrupting data, and the error severely impacts the user's operations." +
              "<br><br><b>Severity 2</b> - Major problem<br>An important function of the system " +
              "is not available in production, and the user's operations are restricted." +
              "<br><br><b>Severity 3</b> - Minor problem<br>Inability to use a function of the " +
              "system occurs, but it does not seriously affect the user's operations.";

        public HelpCanvas(String id) {
            setID(id);
            setPadding(10);
            setOverflow(Overflow.AUTO);
            setContents(contents);
        }
    }

    public SourceViewWidget getSourceView() {
        return mainCanvas;
    }

    public static Application get() {
        return INSTANCE;
    }
}
