/*
 * This code was generated by ojc.
 */
/*
 * MixinedClass.oj
 *
 * An OpenJava example for incorporating mixin mechanism.
 *
 * @author   Michiaki Tatsubori
 * @version  %VERSION% %DATE%
 * @see      java.lang.Object
 *
 * COPYRIGHT 1999 by Michiaki Tatsubori, ALL RIGHTS RESERVED.
 */
package examples.mixin;


import java.lang.Object;
import openjava.mop.*;
import openjava.ptree.*;
import openjava.syntax.*;


/**
 * The metaclass <code>MixinedClass</code> supports classes
 * using mixin.
 * <p>
 * For example, the class <code>MixinedClass</code>:
 * <pre>
 * public class TextboxWithUndo instantiates MixinedClass
 *    extends Textbox mixs Undo
 * {
 * }
 * </pre>
 * would be automatically implemented.
 * <p>
 *
 * @author   Michiaki Tatsubori
 * @version  1.0
 * @since    $Id: MixinedClass.java,v 1.2 2003/02/19 02:55:01 tatsubori Exp $
 * @see java.lang.Object
 */
public class MixinedClass extends OJClass
{

    public static final String KEY_MIXES = "mixes";

    /* overrides for translation */
    public void translateDefinition()
        throws MOPException
    {
        OJClass[] mixinClasses = getMixins();
        for (int i = 0; i < mixinClasses.length; ++i) {
            incorporateMixin( mixinClasses[i] );
        }
    }

    public void incorporateMixin( OJClass mixinClazz )
        throws MOPException
    {
        addInterface( mixinClazz );
        OJMethod[] mixeds = mixinClazz.getMethods();
        for (int i = 0; i < mixeds.length; ++i) {
            addMethod( makeEmptyMethod( mixeds[i] ) );
        }
    }

    /**
     * Generates a empty method with specified name.
     *
     * @param  name  generating method's name.
     * @param forwarded  method to which the generated method forwards.
     * @return  a generated empty method.
     */
    private OJMethod makeEmptyMethod( OJMethod implementee )
        throws MOPException
    {
        StatementList body = new StatementList();
        if (implementee.getReturnType() == OJSystem.VOID) {
            body.add( new ReturnStatement() );
        } else {
            body.add( new ReturnStatement( Literal.constantNull() ) );
        }
        return new OJMethod( this, implementee.getModifiers().remove( OJModifier.ABSTRACT ), implementee.getReturnType(), implementee.getName(), implementee.getParameterTypes(), implementee.getExceptionTypes(), body );
    }

    /* extended information */
    private OJClass[] getMixins()
        throws MOPException
    {
        ObjectList suffix = (ObjectList) getSuffix( KEY_MIXES );
        OJClass[] result = new OJClass[suffix.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = OJClass.forName( suffix.get( i ).toString() );
        }
        return result;
    }

    /* override to extend syntax */
    public static boolean isRegisteredKeyword( String keyword )
    {
        return keyword.equals( KEY_MIXES );
    }

    /* override to extend syntax */
    public static SyntaxRule getDeclSuffixRule( String keyword )
    {
        if (keyword.equals( KEY_MIXES )) {
            return new DefaultListRule( new TypeNameRule(), TokenID.COMMA );
        }
        return null;
    }

    public MixinedClass( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
    {
        super( oj_param0, oj_param1, oj_param2 );
    }

    public MixinedClass( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
    {
        super( oj_param0, oj_param1 );
    }

}
