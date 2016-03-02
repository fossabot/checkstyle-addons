## RegexpOnString

This check applies a regular expression to String literals found in the source.

This is useful in order to ensure that Strings do not contain illegal content such as hard-coded host names, IP addresses, known user names, or improperly encoded characters.


### Properties

<dl>
<dt><span class="propname">regexp</span>
    <span class="proptype"><a href="http://checkstyle.sourceforge.net/property_types.html#regexp">regular expression</a></span></dt>
<dd><span class="propdesc">The regular expression applied to each String literal found in a source file.</span>
    <span class="propdefault"><code>^(?!x)x</code> (check disabled)</span></dd>
</dl>


#### Custom Messages

In addition to the properties, optionally adding a `message` element may benefit this check to make the warning easier to understand. The message key is `regexp.string`. The message text can make use of placeholders `{0}` (the String literal in question, excluding quotes) and `{1}` (the regular expression used by the matcher).


### Examples

To check for some hard-coded host names, including an optional custom message text:

{% highlight xml %}
<module name="RegexpOnString">
  <property name="regexp" value="(?:localhost|\.mydomain\.com)"/>
  <message key="regexp.string" value="String &quot;{0}&quot; appears to contain a hard-coded hostname."/>
</module>
{% endhighlight %}

The following configuration finds hard-coded IPv4 addresses:

{% highlight xml %}
<module name="RegexpOnString">
  <property name="regexp" value="\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b"/>
  <message key="regexp.string" value="String &quot;{0}&quot; appears to contain a hard-coded IP address."/>
</module>
{% endhighlight %}


### Parent Module

[TreeWalker](http://checkstyle.sourceforge.net/config.html#TreeWalker)