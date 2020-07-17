import java.io.*;
import java.util.*;

public class MyNode<T>
{
  private T key; //can be a letter or $ or '
  private MyNode<T> sibling;
  private MyNode<T> child;

  public MyNode()
  {
    key = null;
    sibling = null;
    child = null;
  }

  public T getKey()
  {
    return key;
  }

  public MyNode<T> getSibling()
  {
    return sibling;
  }

  public MyNode<T> getChild()
  {
    return child;
  }

  public void setKey(T k)
  {
     this.key = k;
  }

  public void setSibling(MyNode<T> s)
  {
    this.sibling = s;
  }

  public void setChild (MyNode<T> c)
  {
    this.child = c;
  }

  public boolean hasChild() //will indicate if there is a child
  {
    return (child != null);
  }

  public boolean hasSib() //will indicate if there is a sibling
  {
    return (sibling != null);
  }
  public String toString()
  {
	  return "" + getKey();
  }

} //EOF
