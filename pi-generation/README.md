# pi generation

this was an attempt to generate a list of pi-digits.

it did not turn out successful, there is a pi-digits.txt in `/digits/` instead.
these numbers were taken from [here][blog] and [here][ia] and [split up][so-split]:

```shell
split -C 40m --numeric-suffixes Pi\ -\ Dec.txt pi-digits- --additional-suffix=.txt
head -c 40000 digits/pi-digits-00.txt > digits/pi-digits.txt
```

[blog]: https://pi2e.ch/blog/2017/03/10/pi-digits-download/#download
[ia]: https://archive.org/details/Math_Constants
[so-split]: https://stackoverflow.com/a/2016918
