package kernbeisser;

import kernbeisser.CustomComponents.ColumnTransformer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Tools {
    private static Toolkit toolkit = Toolkit.getDefaultToolkit();
    public static <A extends Annotation> Collection<Field> getWithAnnotation(Class pattern, Class<A> annotation) {
        ArrayList<Field> out = new ArrayList<>();
        for (Field field : pattern.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(annotation)) out.add(field);
        }
        for (Field field : pattern.getSuperclass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(annotation)) out.add(field);
        }
        return out;
    }
    public static <T> String toSting(T[] in,Function<T,String> transformer){
        StringBuilder stringBuilder = new StringBuilder();
        for (T t : in) {
            stringBuilder.append(transformer.apply(t));
        }
        return stringBuilder.toString();
    }
    public static <T> String toSting(List<T> in, Function<T,String> transformer){
        StringBuilder stringBuilder = new StringBuilder();
        for (T t : in) {
            stringBuilder.append(transformer.apply(t));
        }
        return stringBuilder.toString();
    }
    public static <R,T> R build(List<T> in,R r,BiFunction<R,T,R> builder){
        for (T t : in) {
            r = builder.apply(r,t);
        }
        return r;
    }
    public static int getScreenWidth(){
        return toolkit.getScreenSize().width;
    }
    public static int getScreenHeight(){
        return toolkit.getScreenSize().height;
    }
    public static int add(Integer[] x){
        int o = 0;
        for (Integer i : x) {
            if(i!=null)
                o+=i;
        }
        return o;
    }
    public static void setDoubleFilter(JTextComponent c){
        ((AbstractDocument)c.getDocument()).setDocumentFilter(new DocumentFilter(){
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if(!(fb.getDocument().getText(0,fb.getDocument().getLength()).contains(".")&&text.matches("[,.]")))
                    fb.replace(offset,length,text.replaceAll("[\\D&&[^,.]]","").replaceAll(",","."),attrs);
            }
        });
    }
    public static void setRealNumberFilter(JTextComponent c){
        ((AbstractDocument)c.getDocument()).setDocumentFilter(new DocumentFilter(){
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if(!(fb.getDocument().getText(0,fb.getDocument().getLength()).contains(".")&&text.matches("[,.]")))
                    fb.replace(offset,length,text.replaceAll("[^\\d]",""),attrs);
            }
        });
    }
    public static void ping(JComponent component){
        ping(component,false);
    }
    public static void ping(JComponent component,boolean forceGround){
        new Thread(() -> {
            Toolkit.getDefaultToolkit().beep();
            component.requestFocus();
            for (int i = forceGround ? 0 : 100; i < 256; i++) {
                try {
                    Thread.sleep(4);
                    if(forceGround)
                        component.setForeground(new Color(255-i,0,0));
                    else
                        component.setBackground(new Color(255,i,i));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    public static <T,O extends Collection> O extract(Supplier<O> supplier, String s, String separator, Function<String,T> method){
        String[] columns  = s.split(separator);
        O out = supplier.get();
        for (String column : columns) {
            out.add(method.apply(column));
        }
        return out;
    }
    public static <T> T[] extract(Class<T> c,String s, String separator, Function<String,T> method){
        return extract(ArrayList::new,s,separator,method).toArray((T[])Array.newInstance(c,0));
    }
    public static <I,O> O[] transform(I[] in,Class<O> out,Function<I,O> transformer){
        O[] output = (O[]) Array.newInstance(out,in.length);
        for (int i = 0; i < in.length; i++) {
            output[i]=transformer.apply(in[i]);
        }
        return output;
    }
    public static  <T> Function<String,T> findParser(Class<T> c){
        if(c.equals(Boolean.class)||c.equals(boolean.class))return e -> e.equals("null")?null:c.cast(Boolean.parseBoolean(e));
        else
        if(c.equals(Integer.class)||c.equals(int.class))return e -> e.equals("null")?null:c.cast(Integer.parseInt(e));
        else
        if(c.equals(Float.class)||c.equals(float.class)) return e -> e.equals("null")?null:c.cast(Float.parseFloat(e));
        else
        if(c.equals(Double.class)||c.equals(double.class))return e -> e.equals("null")?null:c.cast(Double.parseDouble(e));
        else return c::cast;
    }
    public static <T> void forEach(T[] o, Consumer<T> consumer){
        for (T object : o) {
            consumer.accept(object);
        }
    }
    public static <T> Collection<T> createCollection(Supplier<T> s,int size){
        Collection<T> out = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            out.add(s.get());
        }
        return out;
    }
    public static <I,O> O overwrite(O out,I in){
        Class oc = out.getClass();
        for (Field declaredField : in.getClass().getDeclaredFields()) {
            try {
                Field target = oc.getDeclaredField(declaredField.getName());
                target.setAccessible(true);
                declaredField.setAccessible(true);
                target.set(out,declaredField.get(in));
            } catch (NoSuchFieldException | IllegalAccessException e) { }
        }
        return out;
    }
    public static ColumnTransformer priceTransformer(){
        return s -> Integer.parseInt(s)/100f+"\u20AC";
    }
}
