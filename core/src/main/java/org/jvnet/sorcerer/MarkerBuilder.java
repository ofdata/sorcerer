package org.jvnet.sorcerer;

import antlr.Token;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import java.util.Set;

/**
 * Base class for adding markers to the {@link HtmlGenerator}.
 *
 * @see ParsedSourceSet
 */
abstract class MarkerBuilder<R,P> extends TreeScanner<R,P> {
    private final CompilationUnitTree cu;
    private final LineMap lineMap;
    private final HtmlGenerator gen;
    private final LinkResolver linkResolver;
    private final SourcePositions srcPos;
    private final Elements elements;

    public MarkerBuilder(CompilationUnitTree cu, HtmlGenerator gen, LinkResolver linkResolver,SourcePositions srcPos,Elements elements) {
        this.cu = cu;
        this.gen = gen;
        this.lineMap = cu.getLineMap();
        this.linkResolver = linkResolver;
        this.srcPos = srcPos;
        this.elements = elements;
    }

    private String buildId(Element e) {
        String buf = linkResolver.href(e);
        if(buf.length()==0)
            return null; // no ID
        if(buf.charAt(0)!='#')
            throw new IllegalStateException("Computed ID for "+e+" is "+buf);
        return buf.substring(1);
    }

    /**
     * Adds a declaration marker.
     */
    protected final void addDecl(Tree t,Element e) {
        String id = buildId(e);
        gen.add(new TagMarker(cu,srcPos,t,'#'+id,getCssClass(e,"d"),id,null));
    }

    protected final void addDecl(Token t,Element e) {
        long sp = lineMap.getPosition(t.getLine(),t.getColumn());
        long ep = sp+t.getText().length();
        String id = buildId(e);
        gen.add(new TagMarker(sp,ep, '#'+id,getCssClass(e,"d"),id,null));
    }

    /**
     * Adds a reference marker.
     */
    protected final void addRef(Tree t,Element e) {
        gen.add(new TagMarker(cu,srcPos,t,linkResolver.href(e),getCssClass(e,"r"),null,null));
    }

    protected final void addRef(long sp, long ep,Element e) {
        gen.add(new TagMarker(sp,ep,linkResolver.href(e),getCssClass(e,"r"),null,null));
    }

    private String getCssClass(Element e, String seed) {
        StringBuilder buf = new StringBuilder(seed);

        // static marker
        Set<Modifier> mods = e.getModifiers();
        if(mods.contains(Modifier.STATIC)) {
            if(buf.length()>0)
                buf.append(' ');
            buf.append("st");
        }

        // deprecated marker
        if(elements.isDeprecated(e)) {
            if(buf.length()>0)
                buf.append(' ');
            buf.append("dp");
        }

        if(buf.length()>0)
            buf.append(' ');

        switch (e.getKind()) {
        case ANNOTATION_TYPE:       buf.append("an"); break;
        case CLASS:                 buf.append("cl"); break;
        case CONSTRUCTOR:           buf.append("co"); break;
        case ENUM:                  buf.append("en"); break;
        case ENUM_CONSTANT:         buf.append("ec"); break;
        case EXCEPTION_PARAMETER:   buf.append("ex"); break;
        case FIELD:                 buf.append("fi"); break;
        case INSTANCE_INIT:         buf.append("ii"); break;
        case INTERFACE:             buf.append("it"); break;
        case LOCAL_VARIABLE:        buf.append("lv"); break;
        case METHOD:                buf.append("me"); break;
        case PACKAGE:               buf.append("pk"); break;
        case PARAMETER:             buf.append("pa"); break;
        case STATIC_INIT:           buf.append("si"); break;
        case TYPE_PARAMETER:        buf.append("tp"); break;
        default:                    break;
        }

        return buf.toString();
    }

}